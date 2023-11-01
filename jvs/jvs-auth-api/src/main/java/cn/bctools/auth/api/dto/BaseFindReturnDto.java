package cn.bctools.auth.api.dto;

import cn.bctools.common.entity.dto.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseFindReturnDto {

    List<UserDto> userDtoList;
    List<SysRoleDto> roleDtoList;
    List<SysDeptDto> deptDtoList;
    List<UserGroupDto> groupDtoList;
    List<SysJobDto> jobDtoList;

}
