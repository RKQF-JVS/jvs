package cn.bctools.dynamic.getter;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.util.DatabaseUtils;
import cn.bctools.dynamic.utl.DynamicDataSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 数据源信息获取抽象类
 * <p>
 * 提供默认的getCurrent()方法
 *
 * @Author: GuoZi
 */
@Slf4j
public abstract class AbstractDynamicDataSourceGetter implements IDynamicDataSourceGetter {

    /**
     * 使用MyBatis的多数据源切换工具进行获取
     *
     * @return 当前线程的数据源信息
     */
    @Override
    public DatabaseInfo getCurrent() {
        DynamicDataSourceProperties prop = SpringContextUtil.getBean(DynamicDataSourceProperties.class);
        Map<String, DataSourceProperty> datasourceMap = prop.getDatasource();
        // 获取当前数据源key
        String key = DynamicDataSourceContextHolder.peek();
        if (StringUtils.isBlank(key)) {
            // key为空时, 当前为主数据源
            key = prop.getPrimary();
            log.debug("当前数据源key为空, 使用主数据源: {}", key);
        } else {
            log.debug("当前数据源key: {}", key);
        }
        DataSourceProperty dataSourceProperty = datasourceMap.get(key);
        if (ObjectNull.isNotNull(dataSourceProperty)) {
            // 普通多数据源配置
            return DatabaseUtils.parseUrl(dataSourceProperty.getUrl());
        }
        log.debug("尝试解析动态数据源");
        try {
            // 动态多数据源信息
            return DynamicDataSourceUtils.parseKey(key);
        } catch (Exception e) {
            log.error("当前数据源信息解析异常, 使用主数据源");
        }
        return DatabaseUtils.parseUrl(datasourceMap.get(prop.getPrimary()).getUrl());
    }

}
