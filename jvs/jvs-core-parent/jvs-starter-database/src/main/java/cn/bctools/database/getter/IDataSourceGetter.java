package cn.bctools.database.getter;

import cn.bctools.database.entity.DatabaseInfo;

/**
 * 数据源获取接口
 *
 * @Author: GuoZi
 */
public interface IDataSourceGetter {

    /**
     * 获取当前线程的数据源
     *
     * @return 数据源信息
     */
    DatabaseInfo getCurrent();

}
