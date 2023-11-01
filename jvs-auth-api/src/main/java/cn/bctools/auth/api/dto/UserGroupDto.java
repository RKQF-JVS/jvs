package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author guojing
 */
@Data
@Accessors(chain = true)
@ApiModel("用户组对象")
public class UserGroupDto {

    @ApiModelProperty("用户组ID")
    private String id;

    @ApiModelProperty("用户组名称")
    private String name;

    @ApiModelProperty("用户ID")
    private List<String> users;

}
