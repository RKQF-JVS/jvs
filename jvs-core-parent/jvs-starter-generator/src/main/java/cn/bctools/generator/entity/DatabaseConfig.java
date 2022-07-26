package cn.bctools.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据源信息
 *
 * @Author: GuoZi
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class DatabaseConfig {

    /**
     * 项目模块名称
     **/
    private String moduleName;
    /**
     * 大驼峰名称
     **/
    private String upperCamelCase;
    /**
     * 根包
     **/
    private String rootPkg;
    /**
     * 表信息
     **/
    private List<TableInfo> tableInfos;

    @Data
    public static class TableInfo {
        /**
         * 项目模块名称
         **/
        private String moduleName;
        /**
         * 项目模块文件夹名称
         **/
        private String modulePathName;
        /**
         * 根包
         **/
        private String rootPkg;
        /**
         * 表解释
         **/
        private String tableInfo;
        /**
         * 表名称
         **/
        private String tableName;
        /**
         * 实体类名称
         **/
        private String entityName;
        /**
         * 表字段
         **/
        private List<TableFields> tableFields;
    }

    @Data
    public static class TableFields {
        /**
         * DB字段名称
         **/
        private String fieldName;
        /**
         * Java字段名称
         **/
        private String name;
        /**
         * 字段描述
         **/
        private String desc;
        /**
         * 字段Java类型
         **/
        private String type;
        /**
         * 是不是数值
         */
        private Boolean isNum;
        /**
         * 是不是主键
         */
        private Boolean isPri;
        /**
         * 逻辑删除字段
         */
        private Boolean isLogicDel;
        /**
         * 时间格式化 datetime date time
         */
        private String timeType;
    }

}
