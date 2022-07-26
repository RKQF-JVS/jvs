package cn.bctools.dynamic.init;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import cn.bctools.database.entity.DataSourceInfo;
import cn.bctools.database.entity.TableInfo;
import cn.bctools.database.getter.DefaultTableFieldGetter;
import cn.bctools.database.getter.ITableFieldGetter;
import cn.bctools.database.mapper.TableInfoMapper;
import cn.bctools.dynamic.getter.DefaultDynamicDataSourceGetter;
import cn.bctools.dynamic.getter.IDynamicDataSourceGetter;
import cn.bctools.dynamic.seata.UndoLogTableCreator;
import cn.bctools.dynamic.utl.DynamicDataSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据源信息初始化
 * <p>
 * 会尝试调用默认实现类:
 * {@link DefaultTableFieldGetter}
 * {@link DefaultDynamicDataSourceGetter}
 *
 * @Author: GuoZi
 */
@Slf4j
public class DynamicDataSourceInit implements ApplicationRunner {

    @Resource
    private TableInfoMapper tableInfoMapper;
    @Resource
    private ITableFieldGetter tableFieldGetter;
    @Resource
    private IDynamicDataSourceGetter databaseLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[dynamic-data] 加载多数据源");
        //获取数据源集
        List<DataSourceInfo> dataSourceInfos = databaseLoader.getAll();
        if (dataSourceInfos.isEmpty()) {
            return;
        }
        int total = dataSourceInfos.size();
        AtomicInteger dataSourceCount = new AtomicInteger(0);
        AtomicInteger tableFieldCount = new AtomicInteger(0);
        boolean defaultTable = tableFieldGetter instanceof DefaultTableFieldGetter;
        if (defaultTable) {
            log.info("获取各数据源的表字段 >>>>");
        }
        dataSourceInfos.parallelStream().forEach(e -> {
            String databaseName = e.getDatabaseName();
            try {
                // 添加数据源
                DynamicDataSourceUtils.addDataSource(e, true);
                if (defaultTable) {
                    DefaultTableFieldGetter defaultTableFieldGetter = (DefaultTableFieldGetter) tableFieldGetter;
                    String dataSourceKey = DynamicDataSourceUtils.generateKey(e);
                    // 切换数据源
                    DynamicDataSourceContextHolder.push(dataSourceKey);
                    List<TableInfo> tableInfos = tableInfoMapper.tableInfo(databaseName);
                    if (ObjectUtils.isEmpty(tableInfos)) {
                        log.debug("字段数据查询为空, 数据库名称: {}, 数据源key: {}, 当前数据源key: {}", databaseName, dataSourceKey, DynamicDataSourceContextHolder.peek());
                    }
                    for (TableInfo tableInfo : tableInfos) {
                        tableInfo.setIp(e.getIp());
                        tableInfo.setPort(e.getPort());
                        tableInfo.setDatabaseName(e.getDatabaseName());
                    }
                    defaultTableFieldGetter.saveCache(tableInfos);
                    dataSourceCount.addAndGet(1);
                    tableFieldCount.addAndGet(tableInfos.size());
                }
            } catch (Throwable ignored) {
            }
        });
        if (defaultTable) {
            log.info(">>>> 数据表加载完毕, 共{}个数据源, 可用数据源{}个, 共{}个表字段", total, dataSourceCount.get(), tableFieldCount.get());
            // 清空线程中的数据源切换信息, 不影响后续请求
            DynamicDataSourceContextHolder.clear();
        }

        log.info("尝试创建undo_log表 >>>>");
        // 创建undo_log表
        ExecutorService service = Executors.newFixedThreadPool(total);
        DynamicDataSourceUtils.gets().values().forEach(e -> service.submit(new UndoLogTableCreator(e)));
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException iex) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
            throw iex;
        }
        log.info(">>>> undo_log表创建完毕");
    }

}
