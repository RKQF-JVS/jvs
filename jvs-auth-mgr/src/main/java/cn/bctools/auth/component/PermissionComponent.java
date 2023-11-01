package cn.bctools.auth.component;

import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.entity.enums.RolePermissionEnum;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.common.utils.TreeUtils;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <>
 *
 * @author auto
 **/
@Slf4j
@Component
@AllArgsConstructor
public class PermissionComponent {

    MenuService menuService;
    RoleService roleService;
    ApplyService applyService;
    TenantService tenantService;
    UserRoleService userRoleService;
    RolePermissionService rolePermissionService;

    /**
     * 获取用户在指定应用下的菜单列表
     *
     * @param userId 用户id
     * @param appKey 应用key
     * @return 菜单列表
     */
    public List<Menu> getMenus(String userId, String appKey) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(appKey)) {
            return Collections.emptyList();
        }
        // 应用校验
        Apply apply = applyService.getOne(Wrappers.<Apply>lambdaQuery().select(Apply::getId).eq(Apply::getAppKey, appKey), false);
        if (ObjectUtil.isNull(apply)) {
            log.warn("应用不存在, appKey: {}", appKey);
            return Collections.emptyList();
        }
        String tenantId = TenantContextHolder.getTenantId();
        TenantPo tenantPo = tenantService.getOne(Wrappers.<TenantPo>lambdaQuery().eq(TenantPo::getId, tenantId), false);
        if (Objects.isNull(tenantPo)) {
            log.warn("租户不存在, tenantId: {}", tenantId);
            return Collections.emptyList();
        }
        // 获取角色id集合
        Set<String> roleIdSet;
        String adminUserId = tenantPo.getAdminUserId();
        if (userId.equals(adminUserId)) {
            // 超级管理员, 获取该租户下所有用户角色 + 当前租户角色
            List<Role> roleList = roleService.list(Wrappers.<Role>lambdaQuery().eq(Role::getType, RoleTypeEnum.userRole));
            //判断是否是超级管理员
            Role any = roleService.getOne(Wrappers.query(new Role().setType(RoleTypeEnum.tenantRole).setRoleName(SysRoleEnum.SYS_ADMIN.getName())));
            //如果是超级管理员，则直接返回所有权限
            if (ObjectNull.isNotNull(any)) {
                // 获取菜单列表
                return menuService.list(Wrappers.<Menu>lambdaQuery().eq(Menu::getApplyId, apply.getId()));
            }
            roleIdSet = roleList.stream().map(Role::getId).collect(Collectors.toSet());
            List<UserRole> userRoles = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, tenantId));
            roleIdSet.addAll(userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet()));
        } else {
            // 普通用户, 获取用户角色
            List<UserRole> userRoles = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId));
            roleIdSet = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        }
        if (ObjectUtils.isEmpty(roleIdSet)) {
            return Collections.emptyList();
        }
        // 获取菜单权限列表
        List<RolePermission> rolePermissionList = rolePermissionService.list(Wrappers.<RolePermission>lambdaQuery()
                .eq(RolePermission::getType, RolePermissionEnum.menu)
                .in(RolePermission::getRoleId, roleIdSet));
        Set<String> permissionIdSet = rolePermissionList.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        if (permissionIdSet.isEmpty()) {
            return Collections.emptyList();
        }
        // 获取菜单列表
        return menuService.list(Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getApplyId, apply.getId())
                .in(ObjectUtil.isNotEmpty(permissionIdSet), Menu::getId, permissionIdSet));
    }

    /**
     * 获取用户在指定应用下的菜单(树形结构)
     *
     * @param userId 用户id
     * @param appKey 应用key
     * @return 菜单集合
     */
    public List<Tree<Object>> getPermission(String userId, String appKey) {
        // 应用校验
        Apply apply = applyService.getOne(Wrappers.<Apply>lambdaQuery().select(Apply::getId).eq(Apply::getAppKey, appKey), false);
        if (ObjectUtil.isNull(apply)) {
            return Collections.emptyList();
        }
        List<Menu> menuList = this.getMenus(userId, appKey);
        if (ObjectUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        //获取有权限的jvs应用权限菜单

        List<TreePo> menus = menuList.stream()
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());
        // 转树形结构
        return TreeUtils.tree(menus, apply.getId());
    }

}
