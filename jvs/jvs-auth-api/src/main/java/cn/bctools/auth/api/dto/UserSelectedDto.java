package cn.bctools.auth.api.dto;

import cn.bctools.common.entity.dto.UserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 人员选择对象
 *
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@ApiModel("用户选择对象")
public class UserSelectedDto {

    @ApiModelProperty("用户信息")
    private List<UserDto> users;
    @ApiModelProperty("角色对象")
    private List<SysRoleDto> roles;
    @ApiModelProperty("部门对象")
    private List<SysDeptDto> depts;
    @ApiModelProperty("岗位对象")
    private List<SysJobDto> jobs;
    @ApiModelProperty("分组对象")
    private List<UserGroupDto> groups;

}
