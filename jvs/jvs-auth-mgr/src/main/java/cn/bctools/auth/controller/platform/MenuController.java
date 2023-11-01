package cn.bctools.auth.controller.platform;

import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.component.PermissionComponent;
import cn.bctools.auth.entity.Menu;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.entity.RolePermission;
import cn.bctools.auth.entity.enums.RolePermissionEnum;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import cn.bctools.auth.service.*;
import cn.bctools.auth.vo.ApplyMenuVo;
import cn.bctools.auth.vo.MenuExtendVo;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TreeUtils;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.Permission;
import cn.bctools.gateway.entity.enums.PermissionTypeEnum;
import cn.bctools.log.annotation.Log;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yxh
 * @ClassName: MenuController
 * @Description: 菜单控制器
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "菜单管理")
@RestController
@RequestMapping("menu")
public class MenuController {

    MenuService menuService;
    RoleService roleService;
    ApplyService applyService;
    TenantService tenantService;
    UserRoleService userRoleService;
    PermissionService permissionService;
    RolePermissionService rolePermissionService;
    PermissionComponent permissionProcessService;

    @Log
    @ApiOperation(value = "获取应用", notes = "添加菜单的时候，需要此应用ID的值")
    @GetMapping("/applys")
    public R<List<Apply>> applys() {
        List<Apply> list = applyService.list(new LambdaQueryWrapper<Apply>().select(Apply::getId, Apply::getName));
        return R.ok(list);
    }

    @Log
    @PostMapping("/menu")
    @ApiOperation("添加菜单")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> addMenu(@RequestBody @Validated Menu menu) {
        menu.setId(null);
        this.handleLayer(menu);
        menuService.save(menu);
        String menuId = menu.getId();
        // 给当前租户下的应用管理员角色赋予权限
        Role role = roleService.getOne(Wrappers.<Role>lambdaQuery()
                .select(Role::getId)
                .eq(Role::getType, RoleTypeEnum.userRole)
                .eq(Role::getRoleName, SysRoleEnum.APP_ADMIN.getName()));
        if (Objects.nonNull(role)) {
            RolePermission rolePermission = new RolePermission().setRoleId(role.getId()).setType(RolePermissionEnum.menu).setPermissionId(menuId);
            rolePermissionService.save(rolePermission);
        }
        return R.ok(true, "添加成功");
    }

    @Log
    @DeleteMapping("/menu/{id}")
    @ApiOperation(value = "删除菜单", notes = "删除一个菜单的时候,需要判断是否有当前的权限，都可以直接删除，不做二次判断，如果存在有这样的操作，定是管理员非法操作")
    public R<Boolean> delete(@PathVariable String id) {
        menuService.removeById(id);
        return R.ok(true, "删除成功");
    }

    @Log
    @PutMapping("/menu")
    @ApiOperation("修改菜单")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> update(@RequestBody @Validated Menu menu) {
        this.handleLayer(menu);
        menuService.updateById(menu);
        return R.ok(true, "修改成功");
    }

    @Log
    @GetMapping("/all")
    @ApiOperation("菜单列表页")
    @Transactional(rollbackFor = Exception.class)
    public R<List<ApplyMenuVo>> all() {
        // 查询所有应用
        List<Apply> applies = applyService.list(Wrappers.query(new Apply().setEnable(true)));
        // 根据应用查询菜单资源
        Set<String> applyIdSet = applies.stream().map(Apply::getId).collect(Collectors.toSet());
        List<Permission> permissions = permissionService.list(Wrappers.<Permission>lambdaQuery()
                .eq(Permission::getType, PermissionTypeEnum.button)
                .in(Permission::getApplyId, applyIdSet));
        Map<String, List<Permission>> permissionMap = permissions.stream().collect(Collectors.groupingBy(Permission::getMenuId));
        List<TreePo> list = menuService.list(Wrappers.<Menu>lambdaQuery().in(Menu::getApplyId, applyIdSet))
                .stream()
                .map(e ->
                    BeanCopyUtil.copy(e, TreePo.class).setExtend(new MenuExtendVo().setMenu(e).setPermissions(permissionMap.get(e.getId())))
                 )
                .collect(Collectors.toList());
        // 构建应用菜单树形结构
        List<ApplyMenuVo> applyMenuVoList = applies.stream().map(e -> {
                    //转树
                    List<Tree<Object>> menusTree = TreeUtils.tree(list, e.getId());
                    ApplyMenuVo copy = BeanCopyUtil.copy(e, ApplyMenuVo.class);
                    return copy.setMenus(menusTree);
                }
        ).collect(Collectors.toList());
        return R.ok(applyMenuVoList);
    }

    /**
     * 根据父级菜单设置菜单层级
     *
     * @param menu 菜单对象
     */
    private void handleLayer(Menu menu) {
        String parentId = menu.getParentId();
        if (StringUtils.isBlank(parentId) || SysConstant.ROOT_ID.equals(parentId)) {
            // 一级菜单的父级id与应用id一致
            parentId = menu.getApplyId();
            menu.setParentId(parentId);
        }
        Assert.notNull(parentId, "应用不存在");
        if (parentId.equals(menu.getApplyId())) {
            // 如果应用和父级为同级，则为第一层
            menu.setLayer(1);
        } else {
            Menu parentMenu = menuService.getOne(Wrappers.<Menu>lambdaQuery()
                    .select(Menu::getId, Menu::getLayer)
                    .eq(Menu::getId, parentId));
            Assert.notNull(parentMenu, "父级菜单不存在");
            menu.setLayer(parentMenu.getLayer() + 1);
        }
    }

}
