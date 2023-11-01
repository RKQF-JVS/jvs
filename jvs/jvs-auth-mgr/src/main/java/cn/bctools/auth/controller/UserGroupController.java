package cn.bctools.auth.controller;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserGroup;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.UserGroupService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.vo.UserBelongGroupVO;
import cn.bctools.auth.vo.UserVo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@RequestMapping("/user/group")
@AllArgsConstructor
@Api(tags = "用户组")
public class UserGroupController {

    private final UserService userService;
    private final UserGroupService userGroupService;
    private final UserTenantService userTenantService;

    @Log
    @GetMapping("/list")
    @ApiOperation(value = "所有组", notes = "所有组，然后显示在组列表左边")
    public R<List<UserGroup>> list() {
        List<UserGroup> list = userGroupService.list(Wrappers.<UserGroup>lambdaQuery().orderByDesc(UserGroup::getCreateTime));
        return R.ok(list);
    }

    @Log
    @ApiOperation(value = "组下的用户", notes = "组下面所有的用户")
    @GetMapping("/users")
    public R<Page<UserVo>> users(@RequestParam(value = "groupId", required = false) String groupId, Page<UserTenant> page) {
        if (ObjectNull.isNotNull(groupId)) {
            UserGroup group = userGroupService.getById(groupId);
            List<String> groupUserIds = group.getUsers();
            if (ObjectUtils.isNotEmpty(groupUserIds)) {
                userTenantService.page(page, Wrappers.<UserTenant>lambdaQuery().eq(UserTenant::getCancelFlag, false).in(UserTenant::getUserId, groupUserIds));
            }
        } else {
            userTenantService.page(page);
        }
        Set<String> userIdSet = page.getRecords().stream().map(UserTenant::getUserId).collect(Collectors.toSet());
        // 可能没有用户
        Page<UserVo> userPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal(), page.isSearchCount());
        if (ObjectUtil.isEmpty(userIdSet)) {
            return R.ok(userPage);
        }
        Map<String, User> userMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        List<UserVo> list = page.getRecords().stream()
                .map(e -> BeanCopyUtil.copy(UserVo.class, userMap.get(e.getUserId()), e))
                .collect(Collectors.toList());
        userPage.setRecords(list);
        return R.ok(userPage);
    }

    @Log
    @PostMapping
    @ApiOperation("新增")
    public R<?> saveUser(@RequestBody @Validated UserGroup userGroup) {
        String userName = UserCurrentUtils.getRealName();
        userGroup.setId(null);
        userGroup.setUsers(new ArrayList<>());
        userGroup.setCreateBy(userName);
        userGroup.setCreateTime(LocalDateTime.now());
        userGroupService.save(userGroup);
        return R.ok();
    }

    @Log
    @PutMapping
    @ApiOperation("修改")
    @Transactional(rollbackFor = Exception.class)
    public R<?> update(@RequestBody @Validated UserGroup dto) {
        userGroupService.updateById(dto);
        return R.ok();
    }

    @Log
    @DeleteMapping("/{id}")
    @ApiOperation("删除")
    @Transactional(rollbackFor = Exception.class)
    public R<?> deleteUser(@PathVariable String id) {
        userGroupService.removeById(id);
        return R.ok();
    }

    @Log
    @DeleteMapping("/user/{userId}/{groupId}")
    @ApiOperation(value = "用户移除组", notes = "只是移除组，并没有其它操作")
    public R<?> deleteGroupUser(@PathVariable String userId, @PathVariable String groupId) {
        UserTenant byId = userTenantService.getById(userId);
        UserGroup userGroup = userGroupService.getById(groupId);
        userGroup.getUsers().remove(byId.getUserId());
        userGroupService.updateById(userGroup);
        return R.ok();
    }

    @Log
    @PutMapping("/user/{groupId}")
    @ApiOperation(value = "用户添加到某个组", notes = "添加到某个组时，只需要组ID和选择的哪些用户")
    public R<?> putUser(@RequestBody List<String> userId, @PathVariable String groupId) {
        UserGroup userGroup = userGroupService.getById(groupId);
        userGroup.getUsers().addAll(userId);
        userGroupService.updateById(userGroup);
        return R.ok();
    }


    @Log
    @GetMapping("/belong/list")
    @ApiOperation(value = "用户所属所有组")
    public R<List<UserBelongGroupVO>> belongList() {
        List<UserGroup> list = userGroupService.list(Wrappers.<UserGroup>lambdaQuery().apply("JSON_CONTAINS(users, CONCAT('\"', {0}, '\"'))", UserCurrentUtils.getCurrentUser().getId()));
        if (CollectionUtils.isEmpty(list)) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(list.stream()
                .map(g -> BeanCopyUtil.copy(g, UserBelongGroupVO.class).setUserTotal(g.getUsers().size()))
                .collect(Collectors.toList()));
    }
}
