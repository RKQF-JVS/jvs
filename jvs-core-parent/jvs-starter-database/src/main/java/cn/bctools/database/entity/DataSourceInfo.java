package cn.bctools.database.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数据源连接信息
 *
 * @author auto
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DataSourceInfo extends DatabaseInfo {

    @ApiModelProperty("数据源账号")
    private String username;

    @ApiModelProperty("数据源密码")
    private String password;

}
