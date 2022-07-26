package cn.bctools.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据表字段信息
 *
 * @Author: GuoZi
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TableColumnInfo {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 数据名
     */
    private String type;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 枚举类型
     */
    private String columnType;
    /**
     * 库名
     */
    private String tableSchema;
    /**
     * 列名
     */
    private String name;
    /**
     * 数据类型
     */
    private Class<?> dataType;
    /**
     * 键类型PRI URI MUL
     */
    private Boolean primaryKey = false;
    /**
     * 注释
     */
    private String columnComment;
    /**
     * 表单名称
     */
    private String tableInfo;


}
