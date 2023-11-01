package cn.bctools.database.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表信息
 *
 * @author auto
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("数据库字段信息")
public class TableInfo extends DatabaseInfo {

    @ApiModelProperty("表名称")
    private String tableName;

    @ApiModelProperty("字段名称")
    private String fieldName;

}
