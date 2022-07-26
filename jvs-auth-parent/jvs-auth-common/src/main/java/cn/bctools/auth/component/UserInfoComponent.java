package cn.bctools.auth.component;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author guojing
 */
@Component
@Slf4j
@AllArgsConstructor
public class UserInfoComponent {

    DeptService deptService;
    JobService jobService;
    UserRoleService userRoleService;
    RoleService roleService;
    PermissionService permissionService;
    UserService userService;
    DataRoleService dataRoleService;
    TenantService tenantService;
    UserTenantService userTenantService;

    /**
     * 根据用户信息，和租户信息获取资源角色，数据权限对象
     *
     * @param userId    用户ID
     * @param adminFlag 是否是当前租户超级管理员
     * @param tenantId  当前租户
     * @return
     */
    public UserInfoDto<UserDto> getUserInfoDto(String userId, Boolean adminFlag, String tenantId) {
        if (adminFlag) {
            //如果是超级管理员，直接查询租户角色权限
            userId = tenantId;
        }
        //获取资源权限
        List<String> roleIds = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId))
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return new UserInfoDto<>();
        }
        //获取租户的角色权限
        Role designRole = roleService.getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getType, RoleTypeEnum.userRole)
                .eq(Role::getRoleName, SysRoleEnum.APP_ADMIN.getName())
                .in(Role::getId, roleIds));

        List<String> permission = permissionService.queryUserPermission(roleIds)
                .stream()
                .map(Permission::getPermission)
                .collect(Collectors.toList());
        //获取数据权限
        List<DataScopeDto> dataScopeList = dataRoleService.queryUserPermission(roleIds);

        //组装用户对象
        return new UserInfoDto<>()
                .setPermissions(permission)
                .setDataScope(dataScopeList)
                .setRoles(roleIds);
    }

    /**
     * 根据用户信息和租户信息查询扩展信息
     * <p>
     *
     * @param info
     * @param tenantId
     * @return
     */
    public UserDto getUserInfoDto(User info, String tenantId) {
        //此没有租户
        if (ObjectUtil.isEmpty(info)) {
            throw new BusinessException("用户不存在");
        }
        UserTenant tenantServiceOne = userTenantService.getOne(Wrappers.query(new UserTenant().setTenantId(tenantId).setUserId(info.getId())));
        UserDto userDto = BeanCopyUtil.copy(UserDto.class, info, tenantServiceOne);
        userDto.setId(info.getId());
        //判断是否是超级管理员
        TenantPo thisTenantPo = tenantService.getById(tenantId);
        if (ObjectNull.isNotNull(thisTenantPo)) {
            userDto.setAdminFlag(thisTenantPo.getAdminUserId().equals(info.getId()));
        } else {
            userDto.setAdminFlag(false);
        }
        return userDto;
    }

}
