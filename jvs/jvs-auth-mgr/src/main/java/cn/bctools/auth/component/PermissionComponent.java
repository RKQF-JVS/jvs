package cn.bctools.auth.component;

import cn.bctools.auth.entity.Menu;
import cn.bctools.auth.entity.RolePermission;
import cn.bctools.auth.entity.UserRole;
import cn.bctools.auth.entity.enums.RolePermissionEnum;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.*;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
     * @param apply  要获取的应用
     * @return 菜单列表
     */
    public List<Menu> getMenus(String userId, Apply apply) {
        String tenantId = TenantContextHolder.getTenantId();
        TenantPo tenantPo = tenantService.getOne(Wrappers.<TenantPo>lambdaQuery().select(TenantPo::getAdminUserId).eq(TenantPo::getId, tenantId), false);
        if (Objects.isNull(tenantPo)) {
            log.warn("租户不存在, tenantId: {}", tenantId);
            return Collections.emptyList();
        }
        // 获取角色id集合
        Set<String> roleIdSet;
        //如果是平台级管理员， 返回所有的资源
        if (UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            return menuService.list(Wrappers.<Menu>lambdaQuery().eq(Menu::getApplyId, apply.getId()));
        }

        //如果是超级管理员，则返回角色对应的权限
        if (UserCurrentUtils.getCurrentUser().getAdminFlag()) {
            //获取该租户的资源，  去掉租户，获取用户为当前租户id的用户信息
            TenantContextHolder.clear();
            List<UserRole> list = userRoleService.list(Wrappers.query(new UserRole().setUserId(UserCurrentUtils.getCurrentUser().getTenantId())));
            if (ObjectNull.isNull(list)) {
                return Collections.emptyList();
            }
            //获取管理员的菜单资源
            roleIdSet = list.stream().map(e -> e.getRoleId()).collect(Collectors.toSet());
        } else {
            //获取租户的
            // 普通用户, 获取用户角色
            List<UserRole> userRoles = userRoleService.list(Wrappers.<UserRole>lambdaQuery().select(UserRole::getRoleId).eq(UserRole::getUserId, userId));
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
        return menuService.list(Wrappers.<Menu>lambdaQuery().eq(Menu::getApplyId, apply.getId()).in(Menu::getId, permissionIdSet));
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
        Apply apply = applyService.getOne(Wrappers.<Apply>lambdaQuery().select(Apply::getId).eq(Apply::getEnable, true).eq(Apply::getAppKey, appKey), false);
        if (ObjectUtil.isNull(apply)) {
            log.warn("应用不存在, appKey: {}", appKey);
            return Collections.emptyList();
        }
        List<Menu> menuList = this.getMenus(userId, apply);
        if (ObjectUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        //获取有权限的jvs应用权限菜单
        List<TreePo> menus = menuList.stream()
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e).setIsNew(DateCompareUtils.isRecentOneMonth(e.getCreateTime())))
                .collect(Collectors.toList());
        // 转树形结构
        return TreeUtils.tree(menus, apply.getId());
    }

}
