package cn.bctools.database.getter;

import java.util.List;

/**
 * 获取当前项目各个数据源的表结构信息
 * <p>
 * 该接口有默认实现类: {@link DefaultTableFieldGetter}
 *
 * @Author: GuoZi
 */
public interface ITableFieldGetter {

    /**
     * 获取指定数据源下的表字段
     *
     * @param ip           数据库ip
     * @param port         数据库端口
     * @param databaseName 数据库名称
     * @param tableName    表名称
     * @return 字段名称集合
     */
    List<String> getFieldNames(String ip, String port, String databaseName, String tableName);

}
