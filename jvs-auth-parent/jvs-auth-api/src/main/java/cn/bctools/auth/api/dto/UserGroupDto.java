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
    String groupId;

    @ApiModelProperty("用户组名称")
    String name;

    @ApiModelProperty("用户ID")
    List<String> userId;
}
