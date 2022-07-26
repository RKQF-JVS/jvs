package cn.bctools.generator.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据表信息
 *
 * @Author: GuoZi
 */
@Data
@Accessors(chain = true)
public class TableInfo {

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表说明
     */
    private String info;

    /**
     * 字段集合
     */
    private List<TableColumnInfo> columns;

}
