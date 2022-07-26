package cn.bctools.auth.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.DeptService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.common.utils.TreeUtils;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Log
    @ApiOperation(value = "查询所有部门", notes = "组织机构管理, 左侧功能, 可添加部门")
    @GetMapping("/all")
    public R<List<Tree<Object>>> all() {
        //获取部门树
        List<TreePo> list = deptService.list()
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

}
