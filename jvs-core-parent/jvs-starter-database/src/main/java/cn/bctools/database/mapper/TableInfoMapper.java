package cn.bctools.database.mapper;

import cn.bctools.database.entity.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据表信息mapper
 *
 * @author auto
 */
@Mapper
public interface TableInfoMapper {

    /**
     * 获取指定数据库下的数据表字段集合
     *
     * @param databaseName 数据库名称
     * @return 数据表字段集合
     */
    @Select("SELECT t.TABLE_NAME AS table_name,c.COLUMN_NAME AS field_name,t.TABLE_SCHEMA AS table_schema FROM information_schema.`TABLES` t INNER JOIN information_schema.`COLUMNS` c ON t.TABLE_SCHEMA=c.TABLE_SCHEMA AND t.TABLE_NAME=c.TABLE_NAME WHERE t.TABLE_TYPE='BASE TABLE' AND t.TABLE_SCHEMA='${databaseName}'")
    List<TableInfo> tableInfo(@Param("databaseName") String databaseName);

}
