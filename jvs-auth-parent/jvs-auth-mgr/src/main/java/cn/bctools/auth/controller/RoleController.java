package cn.bctools.auth.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.component.PermissionComponent;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.entity.enums.PermissionTypeEnum;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.service.*;
import cn.bctools.auth.vo.UserVo;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.DataDictDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.*;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.redis.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 角色接口
 *
 * @author Administrator
 * @Description: 角色接口
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "角色接口")
@RestController
@RequestMapping("role")
public class RoleController {

    RedisUtils redisUtils;
    UserService userService;
    RoleService roleService;
    DataService dataService;
    MenuService menuService;
    ApplyService applyService;
    StringRedisTemplate redisTemplate;
    TenantService tenantService;
    DataRoleService dataRoleService;
    UserRoleService userRoleService;
    UserTenantService userTenantService;
    PermissionService permissionService;
    RolePermissionService rolePermissionService;
    PermissionComponent permissionComponent;

    @Log
    @GetMapping("/all/{type}")
    @ApiOperation(value = "所有角色", notes = "角色管理, 左侧的角色列表页, 默认显示所有角色, 当前用户可查看的角色都可以看")
    public R<List<Role>> all(@PathVariable RoleTypeEnum type) {
        List<Role> list = roleService.list(Wrappers.<Role>lambdaQuery().eq(Role::getType, type).orderByDesc(Role::getCreateTime));
        return R.ok(list);
    }

    @Log
    @PutMapping("/role/{roleId}")
    @ApiOperation(value = "角色授权", notes = "给角色授权, 不管是组织角色, 还是用户角色, 都使用同一个授权接口,根据角色内容, 控制授权信息, 如果是组织角色, 则归属于组织, 如果是用户角色, 保持与用户相同操作")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> tenantRole(@PathVariable String roleId, @RequestBody List<RolePermission> list) {
        // 获取角色类型
        Role role = roleService.getById(roleId);
        if (role.getRoleName().equals(SysRoleEnum.SYS_ADMIN.getName())) {
            Assert.notNull(role, "系统管理员不允许操作");
        }
        Assert.notNull(role, "该角色不存在");
        // 租户角色, 需要同步该角色下的租户权限
        if (RoleTypeEnum.tenantRole.equals(role.getType())) {
            this.removePermissionOfChildTenant(roleId, list);
        }
        // 删除后重新添加权限
        rolePermissionService.remove(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, roleId));
        rolePermissionService.saveBatch(list);
        return R.ok(true, "授权成功");
    }

    @Log
    @PutMapping("/role/data/{roleId}")
    @ApiOperation(value = "角色授权数据权限", notes = "给角色授权单独的数据权限功能")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> dataRole(@PathVariable String roleId, @RequestBody List<SysDataRole> dataRoles) {
        Role role = roleService.getById(roleId);
        if (role.getRoleName().equals(SysRoleEnum.SYS_ADMIN.getName())) {
            Assert.notNull(role, "系统管理员不允许操作");
        }
        // 清空原有数据权限
        dataRoleService.remove(Wrappers.<SysDataRole>lambdaQuery().eq(SysDataRole::getRoleId, roleId));
        if (ObjectUtils.isNotEmpty(dataRoles)) {
            dataRoles.forEach(e -> e.setRoleId(roleId));
            dataRoleService.saveBatch(dataRoles);
        }
        return R.ok(true, "授权成功");
    }

    @Log
    @GetMapping("/role/data/{roleId}")
    @ApiOperation(value = "获取角色数据权限", notes = "获取此岗位的所有数据权限进行回显")
    @Transactional(rollbackFor = Exception.class)
    public R<List<SysDataRole>> getDataRole(@PathVariable String roleId) {
        List<SysDataRole> list = dataRoleService.list(Wrappers.query(new SysDataRole().setRoleId(roleId)));
        return R.ok(list);
    }

    @Log
    @GetMapping("/role/data")
    @ApiOperation(value = "获取所有数据权限")
    public R getDataRoles() {
        //从缓存中获取数据源信息
        Set<String> keys = redisTemplate.keys(SysConstant.DATASCOPE + "*");
        if (ObjectUtil.isEmpty(keys)) {
            return R.ok(Collections.emptyList());
        }
        List<DataDictDto> dataList = new ArrayList<>();
        for (String key : keys) {
            Object obj = redisUtils.get(String.valueOf(key));
            if (obj instanceof List) {
                dataList.addAll((List<DataDictDto>) obj);
            }
        }
        Map<String, List<DataDictDto>> collect = dataList.stream().collect(Collectors.groupingBy(DataDictDto::getDesc));

        Map<String, List<SysData>> list = dataService.list().stream().peek(e -> e.setDataList(collect.get(e.getName())))
                .collect(Collectors.groupingBy(SysData::getName));
        List<Dict> dictList = new ArrayList<>();
        for (String s : list.keySet()) {
            dictList.add(new Dict().set("key", s).set("value", list.get(s)));
        }
        return R.ok(dictList);
    }

    @Log
    @GetMapping("/role/menus/{roleId}")
    @ApiOperation(value = "获取角色的菜单权限")
    public R<List<RolePermission>> getRole(@PathVariable String roleId) {
        List<RolePermission> list = rolePermissionService.list(Wrappers.query(new RolePermission().setRoleId(roleId)));
        return R.ok(list);
    }

    @Log
    @PutMapping("/role/menus/{roleId}")
    @ApiOperation(value = "给此角色授权")
    @Transactional(rollbackFor = Exception.class)
    public R<List<RolePermission>> getRole(@PathVariable String roleId, @RequestBody List<RolePermission> list) {
        Role role = roleService.getById(roleId);
        if (role.getRoleName().equals(SysRoleEnum.SYS_ADMIN.getName())) {
            Assert.notNull(role, "系统管理员不允许操作");
        }
        //删除之前的
        rolePermissionService.remove(Wrappers.query(new RolePermission().setRoleId(roleId)));
        //添加现在的数据
        rolePermissionService.saveBatch(list);
        return R.ok(list);
    }

    @Log
    @GetMapping("/role/menus")
    @ApiOperation(value = "获取所有菜单")
    public R<List<Tree<Object>>> getMenus() {
        //需要获取当前租户下已有权限的菜单
        String userId = UserCurrentUtils.getUserId();
        Map<String, Apply> ids = applyService.list().stream().collect(Collectors.toMap(Apply::getId, Function.identity()));
        List<Permission> permissions = permissionService.list(Wrappers.<Permission>lambdaQuery()
                .eq(Permission::getType, PermissionTypeEnum.button)
                .in(Permission::getApplyId, ids.keySet()));
        Map<String, List<Permission>> permissionMap = permissions.stream().collect(Collectors.groupingBy(Permission::getMenuId));
        Map<String, List<Menu>> menuMap = ids.values().stream()
                .flatMap(e -> permissionComponent.getMenus(userId, e.getAppKey()).stream())
                .collect(Collectors.groupingBy(Menu::getApplyId));

        List<Tree<Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Menu>> entry : menuMap.entrySet()) {
            String appId = entry.getKey();
            List<TreePo> treePos = entry.getValue().stream().map(e -> BeanCopyUtil.copy(e, TreePo.class))
                    .peek(e -> e.setExtend(permissionMap.get(e.getId())))
                    .collect(Collectors.toList());
            List<Tree<Object>> tree = TreeUtils.tree(treePos, appId);
            if (ObjectNull.isNull(tree)) {
                tree = new ArrayList<>();
            }
            Apply applies = ids.get(appId);
            if (ObjectNull.isNotNull(applies)) {
                Tree<Object> tree1 = new Tree<Object>().setName(applies.getName()).setId(applies.getId()).setChildren(tree);
                list.add(tree1);
            }
        }
        return R.ok(list);
    }

    @Log
    @GetMapping("/user")
    @ApiOperation(value = "用户管理", notes = "角色管理-用户管理, 对用户可实现操作, 当前角色下有哪些用户, 可对用户进行移出,系统默认有游客角色,创建用户时, 用户默认为游客角色, 角色不能删除")
    public R<Page<UserVo>> user(@RequestParam(value = "roleId", required = false) String roleId, Page<UserRole> page) {
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        if (ObjectNull.isNotNull(roleId)) {
            userRoleService.page(page, Wrappers.query(new UserRole().setRoleId(roleId)));
        } else {
            userRoleService.page(page);
        }
        Set<String> ids = page.getRecords().stream().map(UserRole::getUserId).collect(Collectors.toSet());
        Page<UserVo> userPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal(), page.isSearchCount());
        //可能没有用户
        if (ObjectUtil.isEmpty(ids)) {
            return R.ok(userPage);
        }
        Map<String, UserTenant> userTenantMap = userTenantService.list(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getTenantId, currentUser.getTenantId()).in(UserTenant::getUserId, ids)).stream().collect(Collectors.toMap(UserTenant::getUserId, Function.identity()));
        Map<String, User> userMap = userService.listByIds(ids).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        List<UserVo> collect = ids.stream().map(e -> BeanCopyUtil.copy(UserVo.class, userMap.get(e), userTenantMap.get(e))).collect(Collectors.toList());
        //角色下的用户操作
        userPage.setRecords(collect);
        return R.ok(userPage);
    }

    @Log
    @GetMapping("/tenant/user/{roleId}")
    @ApiOperation(value = "组织管理", notes = "角色管理-组织管理")
    public R<Page<TenantPo>> tenant(@PathVariable String roleId, Page<UserRole> page) {
        userRoleService.page(page, Wrappers.query(new UserRole().setRoleId(roleId)));
        Set<String> ids = page.getRecords().stream().map(UserRole::getUserId).collect(Collectors.toSet());
        //可能没有用户
        Page<TenantPo> userPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal(), page.isSearchCount());
        if (ObjectUtil.isEmpty(ids)) {
            return R.ok(userPage);
        }
        List<TenantPo> list = tenantService.listByIds(ids);
        userPage.setRecords(list);
        return R.ok(userPage);
    }

    @Log
    @DeleteMapping("/user/{roleId}/{userId}")
    @ApiOperation(value = "移出用户", notes = "角色管理-移出用户, 对用户移出这个角色")
    public R<Boolean> deleteUser(@PathVariable String roleId, @PathVariable String userId) {
        //将某个用户移出某个角色
        userRoleService.remove(Wrappers.query(new UserRole().setUserId(userId).setRoleId(roleId)));
        return R.ok(true, "移出成功");
    }

    @Log
    @DeleteMapping("/tenant/{roleId}/{tenantId}")
    @ApiOperation(value = "移出租户", notes = "角色管理-移出组织, 对用户移出这个组织")
    public R<Boolean> deleteTenant(@PathVariable String roleId, @PathVariable String tenantId) {
        //将某个用户移出某个角色
        userRoleService.remove(Wrappers.query(new UserRole().setUserId(tenantId).setRoleId(roleId)));
        return R.ok(true, "移出成功");
    }

    @Log
    @PutMapping("/tenant/{roleId}")
    @ApiOperation(value = "组织管理", notes = "角色管理-添加组织,添加一些组织到此角色下")
    public R<Boolean> saveTenant(@PathVariable String roleId, @RequestBody List<String> tenantIds) {
        //将某个用户移出某个角色
        Set<UserRole> collect = tenantIds.stream().map(e -> new UserRole().setUserId(e).setRoleId(roleId)).collect(Collectors.toSet());
        userRoleService.saveBatch(collect);
        return R.ok(true, "添加成功");
    }

    @Log
    @GetMapping("/tenants")
    @ApiOperation(value = "所有组织", notes = "查询所有组织列表")
    public R<List<TenantPo>> tenants() {
        List<TenantPo> list = tenantService.list();
        return R.ok(list);
    }

    @Log
    @PutMapping("/user/{roleId}")
    @ApiOperation(value = "用户管理", notes = "角色管理-添加用户,添加一些用户到此角色下")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> saveUser(@PathVariable String roleId, @RequestBody List<String> userIds) {
        // 将这些用户排除保存用户
        List<String> dbUserIds = userRoleService.list(Wrappers.<UserRole>lambdaQuery().select(UserRole::getUserId)
                .eq(UserRole::getRoleId, roleId)
                .in(UserRole::getUserId, userIds))
                .stream().map(UserRole::getUserId)
                .collect(Collectors.toList());
        // 排除重复的角色用户
        userIds.removeAll(dbUserIds);
        //将某个用户添加到某个角色下
        Set<UserRole> collect = userIds.stream().map(e -> new UserRole().setUserId(e).setRoleId(roleId)).collect(Collectors.toSet());
        userRoleService.saveBatch(collect);
        return R.ok(true, "添加成功");
    }

    @Log
    @PostMapping("/save")
    @ApiOperation(value = "新增角色", notes = "此功能包含有权限操作, 如果没有新增角色")
    public R<Boolean> saveRole(@RequestBody Role role) {
        //判断一个名称是否重复, 保证名称不能重复
        Role one = roleService.getOne(Wrappers.query(new Role().setRoleName(role.getRoleName())));
        if (ObjectUtil.isNotEmpty(one)) {
            return R.failed("角色名称已经存在");
        }
        roleService.save(role);
        return R.ok(true, "新增成功");
    }

    @Log
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除角色", notes = "删除后,中间关联表也会删除, 用户对应的角色信息也会没有")
    public R<Boolean> removeRole(@PathVariable String id) {
        Role role = roleService.getById(id);
        if (SysRoleEnum.containName(role.getRoleName())) {
            return R.failed("系统默认角色不能删除");
        }
        roleService.removeById(id);
        userRoleService.remove(Wrappers.query(new UserRole().setRoleId(id)));
        return R.ok(true, "删除成功");
    }

    @Log
    @PutMapping("/update")
    @ApiOperation(value = "修改角色", notes = "只是修改角色基本信息")
    public R<Boolean> updateRole(@RequestBody Role role) {
        if (SysRoleEnum.containName(role.getRoleName())) {
            return R.failed("该名称已被系统默认角色占用");
        }
        Role oldRole = roleService.getById(role.getId());
        if (SysRoleEnum.containName(oldRole.getRoleName())) {
            return R.failed("系统默认角色不能修改");
        }
        roleService.updateById(role);
        return R.ok(true, "修改成功");
    }

    /**
     * 更新所有子租户下的角色权限
     *
     * @param roleId 角色id
     * @param list   更新后的角色权限id集合
     */
    private void removePermissionOfChildTenant(String roleId, @NotNull List<RolePermission> list) {
        if (StringUtils.isBlank(roleId)) {
            return;
        }
        // 获取子租户id
        Set<String> childTenantIds = tenantService.getChild().stream().map(TenantPo::getId).collect(Collectors.toSet());
        if (ObjectUtils.isEmpty(childTenantIds)) {
            return;
        }
        // 获取被取消勾选的部分
        Set<String> permissionIdSet = list.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        List<RolePermission> rolePermissionList = rolePermissionService.list(Wrappers.<RolePermission>lambdaQuery()
                .select(RolePermission::getPermissionId)
                .eq(RolePermission::getRoleId, roleId));
        Set<String> cancelIds = rolePermissionList.stream()
                .map(RolePermission::getPermissionId)
                .filter(e -> !permissionIdSet.contains(e))
                .collect(Collectors.toSet());
        if (ObjectUtils.isEmpty(cancelIds)) {
            return;
        }
        // 查询子租户下所有的角色
        String tenantId = TenantContextHolder.getTenantId();
        TenantContextHolder.clear();
        List<Role> roleList = roleService.list(Wrappers.<Role>lambdaQuery()
                .select(Role::getId)
                .in(Role::getTenantId, childTenantIds));
        TenantContextHolder.setTenantId(tenantId);
        if (ObjectUtils.isEmpty(roleList)) {
            return;
        }
        Set<String> roleIdSet = roleList.stream().map(Role::getId).collect(Collectors.toSet());
        // 删除所有子租户的角色权限
        rolePermissionService.remove(Wrappers.<RolePermission>lambdaQuery()
                .in(RolePermission::getPermissionId, cancelIds)
                .in(RolePermission::getRoleId, roleIdSet));
    }

}
