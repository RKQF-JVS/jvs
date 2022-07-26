package cn.bctools.database.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据源基本信息
 *
 * @Author: GuoZi
 */
@Data
@Accessors(chain = true)
@ApiModel("数据源基本信息")
public class DatabaseInfo {

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("端口")
    private String port;

    @ApiModelProperty("数据库名称")
    private String databaseName;

}
