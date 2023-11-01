package cn.bctools.auth.component;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserRole;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.gateway.entity.Permission;
import cn.bctools.gateway.entity.TenantPo;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 
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
    UserExtensionService userExtensionService;

    /**
     * 根据用户信息，和租户信息获取资源角色，数据权限对象
     *
     * @param appId    应用id
     * @param tenantId 当前租户
     * @param user     用户基本信息
     * @return 用户详细信息
     */
    public UserInfoDto<UserDto> getUserInfoDto(final String appId, String tenantId, UserDto user) {
        String userId = user.getId();
        boolean isAdmin = Boolean.TRUE.equals(user.getAdminFlag());
        // 如果是超级管理员，直接查询租户角色权限
        String queryId = isAdmin ? tenantId : userId;
        // 获取资源权限
        List<UserRole> roles = userRoleService.list(Wrappers.<UserRole>lambdaQuery()
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId, queryId));
        if (ObjectUtils.isEmpty(roles)) {
            return new UserInfoDto<>();
        }
        List<String> roleIds = roles.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        List<Permission> permissionList = permissionService.queryUserPermission(appId, roleIds);
        List<String> permission = permissionList.stream()
                // 只获取当前应用的信息
                .filter(e -> appId.equals(e.getApplyId()))
                .map(Permission::getPermission)
                .collect(Collectors.toList());
        // 获取数据权限
        List<DataScopeDto> dataScopeList = dataRoleService.queryUserPermission(user, roleIds);
        // 获取子部门id集合
        String deptId = user.getDeptId();
        List<String> childDeptIds = deptService.getAllChildId(deptId);
        // 组装用户对象
        return new UserInfoDto<>()
                .setRoles(roleIds)
                .setPermissions(permission)
                .setDataScope(dataScopeList)
                .setChildDeptIds(childDeptIds);
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
            List<UserRole> userRoles = userRoleService.list(Wrappers.<UserRole>lambdaQuery().in(UserRole::getUserId, userIds));
            List<UserTenant> tenantList = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getUserId, userIds));
            List<UserExtension> userExtensionList = userExtensionService.list(Wrappers.<UserExtension>lambdaQuery().in(UserExtension::getUserId, userIds));
            Map<String, List<UserExtension>> userExtensionMap = userExtensionList.stream().collect(Collectors.groupingBy(UserExtension::getUserId));
            Map<String, UserTenant> userTenantMap = tenantList.stream().collect(Collectors.toMap(UserTenant::getUserId, Function.identity()));
            // 判断是否是超级管理员
            TenantPo thisTenantPo = tenantService.getById(tenantId);
            String adminUserId = thisTenantPo.getAdminUserId();
            for (User user : userList) {
                String userId = user.getId();
                userDto = BeanCopyUtil.copy(UserDto.class, user, userTenantMap.get(userId));
                userDto.setId(userId);
                if (userExtensionMap.containsKey(userId)) {
                    List<UserExtension> userExtensions = userExtensionMap.get(userId);
                    Map<String, Object> map = new HashMap<>(userExtensions.size());
                    for (UserExtension userExtension : userExtensions) {
                        map.put(userExtension.getType(), userExtension);
                    }
                    userDto.setExceptions(map);
                }
                userDto.setAdminFlag(userId.equalsIgnoreCase(adminUserId));
                userDto.setRoleIds(Optional.ofNullable(userRoles).orElse(new ArrayList<>()).stream().filter(r -> userId.equals(r.getUserId())).map(UserRole::getRoleId).collect(Collectors.toList()));
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
