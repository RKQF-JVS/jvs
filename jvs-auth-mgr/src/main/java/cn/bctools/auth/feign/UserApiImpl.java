package cn.bctools.auth.feign;

import cn.bctools.common.utils.*;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.auth.api.dto.SearchUserDto;
import cn.bctools.auth.api.dto.UserGroupDto;
import cn.bctools.auth.api.enums.UserQueryType;
import cn.bctools.auth.component.UserInfoComponent;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户信息获取接口
 * 此接口主要用于根据当前用户获取扩展信息
 *
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "[Feign]用户信息接口")
public class UserApiImpl implements AuthUserServiceApi {

    UserService userService;
    TenantService tenantService;
    UserTenantService userTenantService;
    DeptService deptService;
    JobService jobService;
    RoleService roleService;
    UserRoleService userRoleService;
    UserGroupService userGroupService;
    UserInfoComponent userInfoComponent;

    /**
     * 顶级部门的上级默认id
     */
    private static final String ROOT_PARENT_DEPT_ID = "-1";

    @Override
    @ApiOperation("查询所有用户")
    public R<List<UserDto>> users() {
        List<User> users = userService.list();
        // 用户对象转换
        return R.ok(DozerUtil.mapList(users, UserDto.class));
    }

    @Override
    @ApiOperation("根据用户id查询单个用户")
    public R<UserDto> getById(String userId) {
        if (StringUtils.isBlank(userId)) {
            return R.failed("用户不存在");
        }
        User user = userService.getById(userId);
        String tenantId = TenantContextHolder.getTenantId();
        UserDto userDto = userInfoComponent.getUserInfoDto(user, tenantId);
        // 用户对象转换
        return R.ok(userDto);
    }

    @Override
    @ApiOperation("根据用户id集合查询用户对象集合")
    public R<List<UserDto>> getByIds(List<String> userIds) {
        if (ObjectUtil.isEmpty(userIds)) {
            return R.ok(Collections.emptyList());
        }
        List<User> users = userService.listByIds(userIds);
        String tenantId = TenantContextHolder.getTenantId();
        List<UserDto> result = userInfoComponent.getUserInfoDto(users, tenantId);
        return R.ok(result);
    }

    @Override
    @ApiOperation("根据角色id集合查询用户对象集合")
    public R<List<UserDto>> getByRoleIds(List<String> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            return R.ok(Collections.emptyList());
        }
        // 查询用户信息
        List<User> users = userService.getByRoleIds(roleIds);
        // 用户对象转换
        return R.ok(DozerUtil.mapList(users, UserDto.class));
    }

    @Override
    @ApiOperation("根据部门id集合查询用户对象集合")
    public R<List<UserDto>> getByDeptIds(List<String> deptIds) {
        return getUsersByTenantInfo(deptIds, UserTenant::getDeptId);
    }

    @Override
    @ApiOperation("根据部门id集合查询用户")
    public R<List<UserDto>> getByDeptId(String deptId) {
        return getByDeptIds(Collections.singletonList(deptId));
    }

    @Override
    @ApiOperation("根据岗位id集合查询用户对象集合")
    public R<List<UserDto>> getByJobIds(List<String> jobIds) {
        return getUsersByTenantInfo(jobIds, UserTenant::getJobId);
    }

    @Override
    @ApiOperation("查询所有用户组信息")
    public R<List<UserGroupDto>> userGroups() {
        List<UserGroup> groups = userGroupService.list();
        // 用户群组对象转换
        return R.ok(DozerUtil.mapList(groups, UserGroupDto.class));
    }

    @Override
    @ApiOperation("根据用户id查询该用户所在的用户组集合")
    public R<List<UserGroupDto>> userGroup(String userId) {
        if (StringUtils.isBlank(userId)) {
            return R.ok(Collections.emptyList());
        }
        List<UserGroup> groups = userGroupService.list();
        groups.removeIf(group -> ObjectUtils.isEmpty(group.getUsers()) || !group.getUsers().contains(userId));
        // 用户群组对象转换
        return R.ok(DozerUtil.mapList(groups, UserGroupDto.class));
    }

    @Override
    @ApiOperation("根据用户组id查询该组的所有用户")
    public R<List<UserDto>> userGroupsUser(String groupId) {
        UserGroup userGroup = userGroupService.getById(groupId);
        List<String> userIds = userGroup.getUsers();
        return this.getByIds(userIds);
    }

    @Override
    @ApiOperation("用户复杂查询")
    @Transactional(rollbackFor = Exception.class)
    public R<List<UserDto>> userSearch(SearchUserDto dto) {
        UserQueryType type = dto.getType();
        if (Objects.isNull(type)) {
            return R.ok(Collections.emptyList());
        }
        boolean isAnd;
        switch (type) {
            case all:
                return this.users();
            case and:
                isAnd = true;
                break;
            case or:
                isAnd = false;
                break;
            default:
                return R.ok(Collections.emptyList());
        }
        boolean hasCondition = false;
        List<String> userIdCondition = new ArrayList<>();
        // 用户基本信息
        List<String> userInfoIds = this.searchByUserInfo(dto, isAnd);
        if (ObjectNull.isNotNull(userInfoIds)) {
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, userInfoIds, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        // 用户id集
        List<String> userIds = dto.getUserIds();
        if (ObjectUtils.isNotEmpty(userIds)) {
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, userIds, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        // 角色id集
        List<String> roleIds = dto.getRoleIds();
        if (ObjectUtils.isNotEmpty(roleIds)) {
            List<String> ids = userRoleService.list(Wrappers.<UserRole>lambdaQuery().in(UserRole::getRoleId, roleIds)).stream().map(UserRole::getUserId).collect(Collectors.toList());
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, ids, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        // 部门id集
        List<String> deptIds = dto.getDeptIds();
        if (ObjectUtils.isNotEmpty(deptIds)) {
            List<String> ids = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getDeptId, deptIds)).stream().map(UserTenant::getUserId).collect(Collectors.toList());
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, ids, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        // 部门id集(部门负责人)
        List<String> deptLeaderIds = dto.getDeptLeaderIds();
        if (ObjectUtils.isNotEmpty(deptLeaderIds)) {
            List<String> ids = deptService.list(Wrappers.<Dept>lambdaQuery().in(Dept::getId, deptLeaderIds)).stream().map(Dept::getLeaderId).collect(Collectors.toList());
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, ids, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        // 岗位id集
        List<String> jobIds = dto.getJobIds();
        if (ObjectUtils.isNotEmpty(jobIds)) {
            List<String> ids = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getJobId, jobIds)).stream().map(UserTenant::getUserId).collect(Collectors.toList());
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, ids, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        // 群组id集
        List<String> groupIds = dto.getGroupIds();
        if (ObjectUtils.isNotEmpty(groupIds)) {
            List<String> ids = userGroupService.list(Wrappers.<UserGroup>lambdaQuery().in(UserGroup::getId, groupIds)).stream().flatMap(group -> group.getUsers().stream()).collect(Collectors.toList());
            boolean isEmptyResult = this.handleSearchResult(userIdCondition, ids, isAnd, hasCondition);
            if (isEmptyResult) {
                return R.ok(Collections.emptyList());
            }
            hasCondition = true;
        }
        if (!hasCondition) {
            log.info("[用户复杂查询] 查询条件为空, 返回所有用户信息");
            return this.users();
        }
        return this.getByIds(new ArrayList<>(userIdCondition));
    }

    @Override
    @ApiOperation("根据部门id查询该部门的负责人")
    public R<UserDto> getDeptLeaderById(String deptId) {
        if (StringUtils.isBlank(deptId)) {
            return R.failed("部门id为空");
        }
        Dept dept = deptService.getOne(Wrappers.<Dept>lambdaQuery().select(Dept::getLeaderId).eq(Dept::getId, deptId));
        if (Objects.isNull(dept)) {
            return R.failed("部门不存在");
        }
        String leaderId = dept.getLeaderId();
        if (StringUtils.isBlank(leaderId)) {
            return R.failed("该部门没有负责人");
        }
        return this.getById(leaderId);
    }

    @Override
    @ApiOperation("根据部门id查询其上级部门的负责人")
    public R<UserDto> getUpperDeptLeaderById(String deptId) {
        if (StringUtils.isBlank(deptId)) {
            return R.failed("部门id为空");
        }
        Dept dept = deptService.getOne(Wrappers.<Dept>lambdaQuery().select(Dept::getParentId).eq(Dept::getId, deptId));
        if (Objects.isNull(dept)) {
            return R.failed("部门不存在");
        }
        String parentId = dept.getParentId();
        if (StringUtils.isBlank(parentId) || ROOT_PARENT_DEPT_ID.equals(parentId)) {
            return R.failed("该部门没有上级部门");
        }
        return this.getDeptLeaderById(parentId);
    }

    @Override
    @ApiOperation("根据真实姓名查询用户(模糊查询)")
    public R<List<UserDto>> getByRealName(String realName) {
        List<User> users = userService.list(Wrappers.<User>lambdaQuery().like(StringUtils.isNotBlank(realName), User::getRealName, realName));
        String tenantId = UserCurrentUtils.getCurrentUser().getTenantId();
        List<UserDto> result = users.stream()
                .map(info -> userInfoComponent.getUserInfoDto(info, tenantId))
                .collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    @ApiOperation("根据用户id注销用户")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> userDelete(String userId) {
        if (StringUtils.isBlank(userId)) {
            return R.failed("用户id为空");
        }
        int count = userService.count(Wrappers.<User>lambdaQuery().eq(User::getId, userId));
        if (count == 0) {
            return R.failed("用户不存在");
        }
        this.clearUser(userId);
        return R.ok(true);
    }

    @Override
    @ApiOperation("根据用户id修改头像")
    public R<Boolean> updateAvatar(String userId, String avatarUrl) {
        if (StringUtils.isBlank(userId)) {
            return R.failed("用户id为空");
        }
        userService.update(Wrappers.<User>lambdaUpdate().set(User::getHeadImg, avatarUrl).eq(User::getId, userId));
        return R.ok(true);
    }

    @Override
    @ApiOperation("根据id查询用户信息")
    public R<UserDto> getBasicInfoById(String id) {
        // 忽略租户
        User user = userService.getById(id);
        UserTenant tenantServiceOne = userTenantService.getOne(Wrappers.lambdaQuery(new UserTenant().setUserId(id)));
        if (Objects.isNull(user)) {
            log.info("用户不存在, 用户id: {}", id);
            return R.failed("用户不存在");
        }
        return R.ok(BeanCopyUtil.copy(UserDto.class, user, tenantServiceOne));
    }

    /**
     * 通过对应租户信息查询用户集合
     *
     * @param idList        目标租户信息id集合
     * @param getTenantInfo 指定租户信息
     * @param <T>           租户信息类型
     * @return 用户信息集合
     */
    private <T> R<List<UserDto>> getUsersByTenantInfo(List<T> idList, SFunction<UserTenant, T> getTenantInfo) {
        if (ObjectUtils.isEmpty(idList) || Objects.isNull(getTenantInfo)) {
            return R.ok(Collections.emptyList());
        }
        List<UserTenant> userTenantList = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(getTenantInfo, idList));
        List<String> userIds = userTenantList.stream().map(UserTenant::getUserId).collect(Collectors.toList());
        return this.getByIds(userIds);
    }

    /**
     * 根据用户基本信息查询用户id集合
     *
     * @param dto   用户搜索条件
     * @param isAnd true: 交集, false: 并集
     * @return 用户id集合, 搜索条件为空时返回null
     */
    private List<String> searchByUserInfo(@NotNull SearchUserDto dto, boolean isAnd) {
        String accountName = dto.getAccountName();
        String realName = dto.getRealName();
        String phone = dto.getPhone();
        String email = dto.getEmail();
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery()
                .select(User::getId)
                .eq(StringUtils.isNotBlank(accountName), User::getAccountName, accountName)
                .or(!isAnd)
                .eq(StringUtils.isNotBlank(realName), User::getRealName, realName)
                .or(!isAnd)
                .eq(StringUtils.isNotBlank(phone), User::getPhone, phone)
                .or(!isAnd)
                .eq(StringUtils.isNotBlank(email), User::getEmail, email);
        if (wrapper.isEmptyOfWhere()) {
            return null;
        }
        return userService.list(wrapper).stream().map(User::getId).collect(Collectors.toList());
    }

    /**
     * 对两个集合进行交并集操作
     *
     * @param currentList  当前集合
     * @param newList      新集合
     * @param isAnd        true: 交集, false: 并集
     * @param hasCondition 是否强行使用并集
     * @return 取交集的结果集是否为空
     */
    private boolean handleSearchResult(@NotNull List<String> currentList, @Nullable List<String> newList, boolean isAnd, boolean hasCondition) {
        if (Objects.nonNull(newList)) {
            if (isAnd && hasCondition) {
                currentList.retainAll(newList);
            } else {
                currentList.addAll(newList);
            }
        }
        return isAnd && currentList.isEmpty();
    }

    /**
     * 移除指定用户信息
     *
     * @param userId 用户id
     */
    private void clearUser(@NotNull String userId) {
        deptService.clearUser(userId);
        userRoleService.clearUser(userId);
        userGroupService.clearUser(userId);
        userTenantService.clearUser(userId);
    }

}
