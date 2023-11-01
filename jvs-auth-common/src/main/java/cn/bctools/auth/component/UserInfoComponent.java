package cn.bctools.auth.component;

import cn.bctools.auth.entity.*;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
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
    public UserInfoDto<UserDto> getUserInfoDto(String appId, String userId, Boolean adminFlag, String tenantId) {
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

        List<String> permission = permissionService.queryUserPermission(roleIds)
                .stream()
                //只获取这一个应用的权限标识其它的不获取
                .filter(e -> appId.equals(e.getApplyId()))
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
     * @param user     用户信息
     * @param tenantId 租户id
     * @return 用户信息
     */
    public UserDto getUserInfoDto(User user, String tenantId) {
        //此没有租户
        return this.getUserInfoDto(Collections.singletonList(user), tenantId).get(0);
    }

    /**
     * 根据用户信息和租户信息查询扩展信息
     * <p>
     *
     * @param userList 用户信息集合
     * @param tenantId 租户id
     * @return 用户信息
     */
    public List<UserDto> getUserInfoDto(List<User> userList, String tenantId) {
        //此没有租户
        if (ObjectUtil.isEmpty(userList)) {
            throw new BusinessException("用户不存在");
        }
        UserDto userDto;
        List<UserDto> result = new ArrayList<>();
        if (StringUtils.isNotBlank(tenantId)) {
            // 查询租户相关信息
            Set<String> userIds = userList.stream().map(User::getId).collect(Collectors.toSet());
            List<UserTenant> tenantList = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getUserId, userIds));
            Map<String, UserTenant> userTenantMap = tenantList.stream().collect(Collectors.toMap(UserTenant::getUserId, Function.identity()));
            // 判断是否是超级管理员
            TenantPo thisTenantPo = tenantService.getById(tenantId);
            String adminUserId = thisTenantPo.getAdminUserId();
            for (User user : userList) {
                String userId = user.getId();
                userDto = BeanCopyUtil.copy(UserDto.class, user, userTenantMap.get(userId));
                userDto.setId(userId);
                userDto.setAdminFlag(userId.equalsIgnoreCase(adminUserId));
                result.add(userDto);
            }
        } else {
            result = userList.stream().map(user -> BeanCopyUtil.copy(user, UserDto.class)).collect(Collectors.toList());
        }
        // 密码不返回
        result.forEach(e -> e.setPassword(null));
        return result;
    }

}
