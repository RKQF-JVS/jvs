package cn.bctools.dynamic.getter;

import cn.bctools.database.entity.DataSourceInfo;
import cn.bctools.database.getter.IDataSourceGetter;

import java.util.List;

/**
 * 多数据源信息获取接口
 *
 * @Author: GuoZi
 */
public interface IDynamicDataSourceGetter extends IDataSourceGetter {

    /**
     * 获取所有数据源的连接信息
     * <p>
     * 在项目启动时会加载所有数据源信息
     * 用于加载数据源、获取表字段信息（默认实现类）、创建undo_log表等
     *
     * @return 数据源信息集合
     */
    List<DataSourceInfo> getAll();

}
