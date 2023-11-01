package cn.bctools.auth.controller;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.AuthSourceEnum;
import cn.bctools.auth.entity.enums.SysConfigFieldEnum;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.auth.login.enums.LoginTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.service.DeptService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.service.impl.SysConfigServiceImpl;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.*;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @ClassName: DeptController
 * @Description: 部门管理(这里用一句话描述这个类的作用)
 */
@Slf4j
@AllArgsConstructor
@Api(value = "部门管理", tags = "部门接口")
@RestController
@RequestMapping("dept")
public class DeptController {

    public static final String ROOT_PARENT_DEPT_ID = "-1";

    DeptService deptService;
    UserService userService;
    UserRoleService userRoleService;
    UserTenantService userTenantService;
    Map<String, LoginHandler> handlerMap;

    @Log
    @ApiOperation(value = "查询所有部门", notes = "组织机构管理, 左侧功能, 可添加部门")
    @GetMapping("/all")
    public R<List<Tree<Object>>> all() {
        // 获取部门树
        List<TreePo> list = deptService.list(Wrappers.<Dept>lambdaQuery().orderByDesc(Dept::getSort))
                .stream()
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());
        List<Tree<Object>> tree = TreeUtils.tree(list, UserCurrentUtils.getCurrentUser().getTenantId());
        return R.ok(tree);
    }

    @Log
    @ApiOperation(value = "部门人员树", notes = "根据部门选择人员, 单选 和多选的操作")
    @GetMapping("/user/tree")
    public R<List<Tree<Object>>> deptUserTree() {
        //获取部门树
        List<TreePo> list = deptService.list()
                .stream()
                .map(e -> BeanCopyUtil.copy(e, TreePo.class))
                .collect(Collectors.toList());
        List<Tree<Object>> tree = TreeUtils.tree(list, UserCurrentUtils.getCurrentUser().getTenantId());
        return R.ok(tree);
    }

    @Log
    @ApiOperation(value = "组织结构树-统计部门人数", notes = "组织结构树，统计各部门人数")
    @GetMapping("/tree/user/total")
    public R<List<Tree<Object>>> getDeptUser() {
        List<Dept> allDeptList = deptService.list();
        if (CollectionUtils.isEmpty(allDeptList)) {
            return R.ok(Collections.emptyList());
        }
        // 查询各部门人员数量
        Map<String, Long> deptUserCount = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().select(UserTenant::getDeptId, UserTenant::getUserId))
                .stream().peek(u -> {
                    if (StringUtils.isBlank(u.getDeptId())) {
                        u.setDeptId("");
                    }
                }).collect(Collectors.groupingBy(UserTenant::getDeptId, Collectors.counting()));
        // 获取部门树
        List<TreePo> list = allDeptList.stream()
                .map(e -> {
                    // 获取当前节点下所有节点(包括当前节点)
                    List<String> childDeptIds = TreeUtils.getListPassingBy(allDeptList, e.getId(), Dept::getId, Dept::getParentId).stream().map(Dept::getId).collect(Collectors.toList());
                    Long userTotal = deptUserCount.entrySet().stream().filter(entry -> childDeptIds.contains(entry.getKey())).mapToLong(Map.Entry::getValue).sum();
                    JSONObject extend = JSON.parseObject(JSON.toJSONString(e));
                    extend.put("userTotal", userTotal);
                    return BeanCopyUtil.copy(e, TreePo.class).setExtend(extend);
                })
                .collect(Collectors.toList());
        List<Tree<Object>> tree = TreeUtils.tree(list, UserCurrentUtils.getCurrentUser().getTenantId());
        return R.ok(tree);
    }

    @Log
    @ApiOperation(value = "添加部门", notes = "组织机构管理, 左侧功能, 添加部门,添加部门只需要选择上级部门, 即可, 输入名称, 选择部门负责人")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> save(@RequestBody @Validated Dept dept) {
        String leaderId = dept.getLeaderId();
        String parentId = dept.getParentId();
        // 顶级部门的上级id默认为当前租户id
        if (StringUtils.isBlank(parentId) || ROOT_PARENT_DEPT_ID.equalsIgnoreCase(parentId)) {
            String tenantId = TenantContextHolder.getTenantId();
            dept.setParentId(tenantId);
        }
        deptService.save(dept);
        // 处理部门负责人
        if (StringUtils.isNotBlank(leaderId)) {
            userTenantService.checkUserId(leaderId);
            userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                    .set(UserTenant::getDeptId, dept.getId())
                    .set(UserTenant::getDeptName, dept.getName())
                    .eq(UserTenant::getUserId, leaderId));
        }
        return R.ok(true, "添加成功");
    }

    @Log
    @PutMapping
    @ApiOperation(value = "修改部门", notes = "修改一个部门, 只修改基本信息")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> update(@RequestBody @Validated Dept dept) {
        String deptId = dept.getId();
        String deptName = dept.getName();
        String leaderId = dept.getLeaderId();
        Dept oldDept = deptService.checkId(deptId);
        // 修改部门信息
        deptService.update(Wrappers.<Dept>lambdaUpdate()
                .set(Dept::getName, deptName)
                .set(Dept::getLeaderId, leaderId)
                .set(Dept::getSort, dept.getSort())
                .eq(Dept::getId, deptId));
        // 同步部门名称
        if (!deptName.equals(oldDept.getName())) {
            userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                    .set(UserTenant::getDeptName, deptName)
                    .eq(UserTenant::getDeptId, deptId));
        }
        // 修改部门负责人信息
        if (StringUtils.isNotBlank(leaderId)) {
            userTenantService.checkUserId(leaderId);
            userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                    .set(UserTenant::getDeptId, deptId)
                    .set(UserTenant::getDeptName, deptName)
                    .eq(UserTenant::getUserId, leaderId));
        }
        return R.ok(true, "修改成功");
    }

    @Log
    @ApiOperation(value = "删除部门", notes = "删除一个部门, 会将用户全部移除部门")
    @DeleteMapping("/{deptId}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> delete(@PathVariable String deptId) {
        // 删除部门信息
        boolean success = deptService.removeById(deptId);
        if (success) {
            // 移除该部门下的用户
            userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                    .set(UserTenant::getDeptId, null)
                    .set(UserTenant::getDeptName, null)
                    .eq(UserTenant::getDeptId, deptId));
        }
        return R.ok(true, "删除成功");
    }

    @Log
    @ApiOperation(value = "设置部门负责人", notes = "部门负责人只是做声明, 目前没有任何作用")
    @PutMapping("/leader/{userId}/{deptId}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> leader(@PathVariable String userId, @PathVariable String deptId) {
        Dept dept = deptService.checkId(deptId);
        dept.setLeaderId(userId);
        deptService.updateById(dept);
        // 更新部门负责人
        userTenantService.checkUserId(userId);
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getDeptId, dept.getId())
                .set(UserTenant::getDeptName, dept.getName())
                .eq(UserTenant::getUserId, userId));
        return R.ok(true, "设置成功");
    }

    @Log
    @DeleteMapping("/user/{userId}")
    @ApiOperation(value = "用户移除部门", notes = "只是移除部门, 并没有其它操作")
    @Transactional(rollbackFor = Exception.class)
    public R<?> deleteUser(@PathVariable String userId) {
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getDeptId, null)
                .set(UserTenant::getDeptName, null)
                .eq(UserTenant::getUserId, userId));
        // 处理部门负责人
        deptService.update(Wrappers.<Dept>lambdaUpdate()
                .set(Dept::getLeaderId, null)
                .eq(Dept::getLeaderId, userId));
        return R.ok(true, "移除成功");
    }

    @Log
    @PutMapping("/user/{deptId}")
    @ApiOperation(value = "用户添加到某个部门", notes = "添加到某个部门时, 只需要部门ID和选择的哪些用户")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> putUser(@RequestBody List<UserTenant> list, @PathVariable String deptId) {
        Dept dept = deptService.checkId(deptId);
        Set<String> userIdSet = list.stream().map(UserTenant::getId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(userIdSet)) {
            return R.failed("请至少选择一个用户");
        }
        // 修改用户信息
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getDeptId, dept.getId())
                .set(UserTenant::getDeptName, dept.getName())
                .in(UserTenant::getUserId, userIdSet));
        return R.ok(true, "添加成功");
    }

    @Log
    @ApiOperation(value = "同步三方应用组织架构到系统")
    @PostMapping("/pull/{type}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> pull(@PathVariable AuthSourceEnum type) {
        AuthConfigUtil.setCurrentAppId("frame");
        UserDto userDto = UserCurrentUtils.getCurrentUser();
        LoginTypeEnum loginType = LoginTypeEnum.getType(type.name());
        if (ObjectNull.isNotNull(loginType)) {
            if (Boolean.FALSE.equals(userDto.getTenantId().equals(AuthConfigUtil.tenantId(loginType.getConfigType())))) {
                return R.failed("域名所属租户与当前租户不同");
            }
        }
        // 获取三方应用组织架构
        LoginHandler loginHandler = handlerMap.get(type.name());
        List<Dept> remoteDepts = loginHandler.getDeptAll();
        SyncUserDto syncUser = loginHandler.getDeptUserAll(remoteDepts);
        // 同步部门
        deptService.pull(UserCurrentUtils.getCurrentUser(), remoteDepts);
        // 同步用户
        userService.pull(syncUser);

        return R.ok(true, "同步成功");
    }

    @Log
    @ApiOperation(value = "获取已开启的自有系统同步组织架构类型")
    @GetMapping("/enable/pull/type")
    @Transactional(rollbackFor = Exception.class)
    public R<JSONObject> enablePullType() {

        return R.ok();
    }
}
