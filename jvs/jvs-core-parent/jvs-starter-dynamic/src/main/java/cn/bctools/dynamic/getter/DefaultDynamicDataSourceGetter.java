package cn.bctools.dynamic.getter;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.entity.DataSourceInfo;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.util.DatabaseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 默认数据源获取类
 * <p>
 * 从MyBatis配置类中获取
 *
 * @Author: GuoZi
 */
@Slf4j
@AllArgsConstructor
public class DefaultDynamicDataSourceGetter extends AbstractDynamicDataSourceGetter {

    @Override
    public List<DataSourceInfo> getAll() {
        DynamicDataSourceProperties prop = SpringContextUtil.getBean(DynamicDataSourceProperties.class);
        Collection<DataSourceProperty> dataSourceList = prop.getDatasource().values();
        if (ObjectUtils.isEmpty(dataSourceList)) {
            return Collections.emptyList();
        }
        List<DataSourceInfo> dataSourceInfoList = new ArrayList<>(dataSourceList.size());
        for (DataSourceProperty value : dataSourceList) {
            DataSourceInfo dataSourceInfo = new DataSourceInfo();
            DatabaseInfo info = DatabaseUtils.parseUrl(value.getUrl());
            dataSourceInfo.setUsername(value.getUsername())
                    .setPassword(value.getPassword())
                    .setIp(info.getIp())
                    .setPort(info.getPort())
                    .setDatabaseName(info.getDatabaseName());
            dataSourceInfoList.add(dataSourceInfo);
        }
        return dataSourceInfoList;
    }

}
