package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SearchUserDto;
import cn.bctools.auth.api.dto.UserGroupDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息获取接口
 *
 * @author gj
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "user")
public interface AuthUserServiceApi {

    String PREFIX = "/api/user";

    /**
     * 查询所有用户
     *
     * @return 用户信息集合
     */
    @GetMapping(PREFIX + "/query/user/all")
    R<List<UserDto>> users();

    /**
     * 根据用户id查询单个用户
     *
     * @param userId 用户id
     * @return 用户信息集合
     */
    @GetMapping(PREFIX + "/query/user/by/id/{userId}")
    R<UserDto> getById(@PathVariable("userId") String userId);

    /**
     * 根据用户id集合查询用户对象集合
     * <p>
     * 仅返回用户id存在的用户, 返回集合的数量、顺序不能保证与参数一致
     *
     * @param userIds 用户id集合
     * @return 用户信息集合
     */
    @PostMapping(PREFIX + "/query/user/by/ids")
    R<List<UserDto>> getByIds(@RequestParam("userIds") List<String> userIds);

    /**
     * 根据角色id集合查询用户对象集合
     *
     * @param roleIds 角色id集合
     * @return 用户信息集合
     */
    @PostMapping(PREFIX + "/query/user/by/role/ids")
    R<List<UserDto>> getByRoleIds(@RequestParam("roleIds") List<String> roleIds);

    /**
     * 根据部门id集合查询用户对象集合
     *
     * @param deptIds 部门id集合
     * @return 用户信息集合
     */
    @PostMapping(PREFIX + "/query/user/by/dept/ids")
    R<List<UserDto>> getByDeptIds(@RequestParam("deptIds") List<String> deptIds);

    /**
     * 通过部门id获取用户数据(无权限,角色数据)
     *
     * @param deptId 部门id
     * @return 用户数据集合
     */
    @GetMapping(PREFIX + "/query/user/by/dept/{deptId}")
    R<List<UserDto>> getByDeptId(@PathVariable("deptId") String deptId);

    /**
     * 根据岗位id集合查询用户对象集合
     *
     * @param jobIds 职位id集合
     * @return 用户信息集合
     */
    @PostMapping(PREFIX + "/query/user/by/job/ids")
    R<List<UserDto>> getByJobIds(@RequestParam("jobIds") List<String> jobIds);

    /**
     * 查询所有用户组信息
     *
     * @return 用户组信息集合
     */
    @GetMapping(PREFIX + "/query/groups/all")
    R<List<UserGroupDto>> userGroups();

    /**
     * 根据用户id查询该用户所在的用户组集合
     *
     * @param userId 用户id
     * @return 用户组信息集合
     */
    @GetMapping(PREFIX + "/query/groups/by/user/id/{userId}")
    R<List<UserGroupDto>> userGroup(@PathVariable("userId") String userId);

    /**
     * 根据用户组id查询该组的所有用户
     *
     * @param groupId 用户组id
     * @return 用户信息集合
     */
    @PutMapping(PREFIX + "/query/group/by/id/{groupId}")
    R<List<UserDto>> userGroupsUser(@PathVariable("groupId") String groupId);

    /**
     * 用户复杂查询
     * <p>
     * 根据条件筛选出对应的人员。角色、部门、职位、群组，进行交集并集的查询处理
     *
     * @param dto 用户搜索条件
     * @return 用户信息集合
     */
    @PostMapping(PREFIX + "/query/user/search")
    R<List<UserDto>> userSearch(@RequestBody SearchUserDto dto);

    /**
     * 根据部门id查询该部门的负责人
     *
     * @param deptId 部门id
     * @return 用户信息
     */
    @GetMapping(PREFIX + "/query/dept/leader/by/id/{deptId}")
    R<UserDto> getDeptLeaderById(@PathVariable("deptId") String deptId);

    /**
     * 根据部门id查询其上级部门的负责人
     *
     * @param deptId 部门id
     * @return 用户信息
     */
    @GetMapping(PREFIX + "/query/upper/dept/leader/by/id/{deptId}")
    R<UserDto> getUpperDeptLeaderById(@PathVariable("deptId") String deptId);

    /**
     * 根据真实姓名查询用户(模糊查询)
     *
     * @param realName 用户真实姓名
     * @return 用户信息集合
     */
    @GetMapping(value = PREFIX + "/query/user/by/realName/{realName}")
    R<List<UserDto>> getByRealName(@PathVariable("realName") String realName);

    /**
     * 根据用户id注销用户
     * <p>
     * 仅删除该用户在当前租户下的信息, 用户本身不会被删除
     *
     * @param userId 用户id
     * @return 删除结果
     */
    @DeleteMapping(PREFIX + "/delete/user/by/id/{userId}")
    R<Boolean> userDelete(@PathVariable("userId") String userId);

    /**
     * 根据用户id修改头像
     *
     * @param userId    用户id
     * @param avatarUrl 用户头像地址(该参数为空时会置空头像地址)
     * @return 修改结果
     */
    @PutMapping(PREFIX + "/update/avatar/by/user/id/{userId}")
    R<Boolean> updateAvatar(@PathVariable("userId") String userId,
                            @RequestParam("avatarUrl") String avatarUrl);

    /**
     * 查询单个用户
     * <p>
     * 1. 查询时忽略租户
     * 2. 只查询用户基本信息,没有角色权限相关信息
     *
     * @param id 用户id
     * @return R
     */
    @GetMapping(value = "/user/get/basic/{id}")
    R<UserDto> getBasicInfoById(@PathVariable("id") String id);
}
