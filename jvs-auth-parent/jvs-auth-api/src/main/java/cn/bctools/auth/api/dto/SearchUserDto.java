package cn.bctools.auth.api.dto;

import cn.bctools.auth.api.enums.UserQueryType;
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
@ApiModel("搜索用户对象")
public class SearchUserDto {

    /**
     * 若该值为null, 则搜索结果会为空
     */
    @ApiModelProperty("筛选关系(默认为交集)")
    UserQueryType type = UserQueryType.and;

    @ApiModelProperty("登录帐号(精确查询)")
    String accountName;
    @ApiModelProperty("用户姓名(精确查询)")
    String realName;
    @ApiModelProperty("手机号(精确查询)")
    String phone;
    @ApiModelProperty("邮箱(精确查询)")
    String email;

    @ApiModelProperty("用户ID集")
    List<String> userIds;
    @ApiModelProperty("角色ID集")
    List<String> roleIds;
    @ApiModelProperty("部门id集")
    List<String> deptIds;
    @ApiModelProperty("部门id集(部门负责人)")
    List<String> deptLeaderIds;
    @ApiModelProperty("岗位ID集")
    List<String> jobIds;
    @ApiModelProperty("群组ID集")
    List<String> groupIds;

}
