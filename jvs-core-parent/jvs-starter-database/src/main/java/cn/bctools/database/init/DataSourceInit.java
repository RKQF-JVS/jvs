package cn.bctools.database.init;

import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.entity.TableInfo;
import cn.bctools.database.getter.DefaultDataSourceGetter;
import cn.bctools.database.getter.DefaultTableFieldGetter;
import cn.bctools.database.getter.IDataSourceGetter;
import cn.bctools.database.getter.ITableFieldGetter;
import cn.bctools.database.mapper.TableInfoMapper;
import cn.bctools.database.util.DatabaseUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * 数据源信息初始化
 * <p>
 * 会尝试调用默认实现类:
 * {@link DefaultTableFieldGetter}
 * {@link DefaultDataSourceGetter}
 *
 * @Author: GuoZi
 */
@Slf4j
public class DataSourceInit extends SpringContextUtil {

    @Resource
    private TableInfoMapper tableInfoMapper;

    @Getter
    private static Boolean isTenantId = false;


    private static final String DDL_SQL_PERFORMANCE = "CREATE TABLE IF NOT EXISTS  `sql_performance` (\n" +
            "\t`id` INT ( 20 ) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "\t`sql` VARCHAR ( 255 ) DEFAULT NULL,\n" +
            "\t`start_time` INT ( 11 ) DEFAULT NULL,\n" +
            "\t`end_time` INT ( 11 ) DEFAULT NULL,\n" +
            "\t`consuming_time` INT ( 11 ) DEFAULT NULL,\n" +
            "\t`access_type` VARCHAR ( 50 ) DEFAULT NULL,\n" +
            "\t`slow_sql` VARCHAR ( 255 ) DEFAULT NULL,\n" +
            "\t`app_name` VARCHAR ( 50 ) DEFAULT NULL,\n" +
            "PRIMARY KEY ( `id` ) \n" +
            ") ENGINE = INNODB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COMMENT = '保存执行计划表';";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        log.info("[mysql-data] 加载单个数据源");
        //初始化时不需要数据权限和租户权限，所以不用清除
        ITableFieldGetter tableFieldGetter = SpringContextUtil.getBean(ITableFieldGetter.class);
        IDataSourceGetter dataSourceGetter = SpringContextUtil.getBean(IDataSourceGetter.class);
        boolean defaultField = tableFieldGetter instanceof DefaultTableFieldGetter;
        boolean defaultSource = dataSourceGetter instanceof DefaultDataSourceGetter;
        if (!defaultField && !defaultSource) {
            return;
        }
        log.info("数据源信息初始化 >>>> ");
        DataSource dataSource = SpringContextUtil.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            connection.createStatement().execute(DDL_SQL_PERFORMANCE);
            DatabaseInfo info = DatabaseUtils.parseUrl(url);
            if (defaultField) {
                List<TableInfo> tableInfos = tableInfoMapper.tableInfo(info.getDatabaseName());
                for (TableInfo tableInfo : tableInfos) {
                    tableInfo.setIp(info.getIp());
                    tableInfo.setPort(info.getPort());
                    tableInfo.setDatabaseName(info.getDatabaseName());
                }
                isTenantId = tableInfos.stream().filter(e -> e.getFieldName().equals("tenant_id")).findAny().isPresent();
                ((DefaultTableFieldGetter) tableFieldGetter).saveCache(tableInfos);
                log.info(">>>> 数据表加载完毕, 共{}个表字段", tableInfos.size());
            }
            if (defaultSource) {
                log.info(">>>> 数据源加载完毕, 共1个数据源");
                ((DefaultDataSourceGetter) dataSourceGetter).init(info);
            }
        } catch (Exception e) {
            log.error(">>>> 数据源信息初始化异常, 部分功能可能无法正常使用", e);
        }
    }

}
