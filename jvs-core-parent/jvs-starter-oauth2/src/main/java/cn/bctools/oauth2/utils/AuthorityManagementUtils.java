package cn.bctools.oauth2.utils;

import cn.bctools.auth.api.api.AuthDeptServiceApi;
import cn.bctools.auth.api.api.AuthRoleServiceApi;
import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.auth.api.dto.SearchUserDto;
import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.R;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 可直接调用静态方法
 * 1、根据用户id获取用户信息
 * 2、查询部门id下所有的用户
 * 3、根据角色id查询用户集合
 * 4、获取某部门下所有部门(子部门)
 * 5、获取所有部门(树结构)
 * 6、根据某个部门查询上级部门
 * 7、根据某个部门查询下面某些职位的用户
 * 8、根据手机号查询用户
 * 9、获取角色信息
 * 10、批量查询用户
 * 11、根据职位查询所有人
 * 12、查询部门负责人
 * 13、查询上级部门负责人
 * 14、查询当前登录用户所在部门的负责人
 * 15、修改用户的租户id
 *
 * @author: GuoZi
 */
@Slf4j
@Order(0)
public class AuthorityManagementUtils {

    @Autowired
    public AuthorityManagementUtils(AuthUserServiceApi userService,
                                    AuthRoleServiceApi roleService,
                                    AuthDeptServiceApi deptService) {
        USER_SERVICE = userService;
        ROLE_SERVICE = roleService;
        DEPT_SERVICE = deptService;
    }

    private static AuthUserServiceApi USER_SERVICE;
    private static AuthRoleServiceApi ROLE_SERVICE;
    private static AuthDeptServiceApi DEPT_SERVICE;

    /**
     * 根据用户id获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    public static UserDto getUserById(String userId) {
        R<UserDto> result = USER_SERVICE.getById(userId);
        return validResult(result, null);
    }

    /**
     * 批量查询用户
     *
     * @param userIds 用户id集合
     * @return 用户信息集合
     */
    public static List<UserDto> getUsersByIds(List<String> userIds) {
        R<List<UserDto>> result = USER_SERVICE.getByIds(userIds);
        return validResult(result, Collections.emptyList());
    }

    /**
     * 查询某部门下所有的用户
     *
     * @param deptId 部门id
     * @return 用户集合
     */
    public static List<UserDto> getUsersByDeptId(String deptId) {
        R<List<UserDto>> result = USER_SERVICE.getByDeptIds(Collections.singletonList(deptId));
        return validResult(result, Collections.emptyList());
    }

    /**
     * 根据角色id查询用户集合
     *
     * @param roleId 角色id
     * @return 用户集合
     */
    public static List<UserDto> getUsersByRoleId(String roleId) {
        R<List<UserDto>> result = USER_SERVICE.getByRoleIds(Collections.singletonList(roleId));
        return validResult(result, Collections.emptyList());
    }

    /**
     * 条件查询
     */
    public static List<UserDto> userSearch(SearchUserDto dto) {
        R<List<UserDto>> result = USER_SERVICE.userSearch(dto);
        return validResult(result, Collections.emptyList());
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    public static UserDto getUserByPhone(String phone) {
        SearchUserDto dto = new SearchUserDto();
        dto.setPhone(phone);
        List<UserDto> data = userSearch(dto);
        if (ObjectUtil.isNotEmpty(data)) {
            return data.get(0);
        }
        return null;
    }

    /**
     * 查询部门负责人
     *
     * @param deptId 部门id
     * @return 用户信息
     */
    public static UserDto getDeptLeader(String deptId) {
        R<UserDto> result = USER_SERVICE.getDeptLeaderById(deptId);
        return validResult(result, null);
    }

    /**
     * 查询上级部门负责人
     *
     * @param deptId 部门id
     * @return 用户信息
     */
    public static UserDto getUpperDeptLeader(String deptId) {
        R<UserDto> result = USER_SERVICE.getUpperDeptLeaderById(deptId);
        return validResult(result, null);
    }

    /**
     * 查询当前登录用户所在部门的负责人
     *
     * @return 用户信息
     */
    public static UserDto getCurrentDeptLeader() {
        String deptId = UserCurrentUtils.getDeptId();
        if (Objects.isNull(deptId)) {
            log.warn("当前用户没有所属部门");
            return null;
        }
        return getDeptLeader(deptId);
    }

    /**
     * 根据部门id查询部门信息
     *
     * @param deptId 部门id
     * @return 部门集合
     */
    public static SysDeptDto getDeptById(String deptId) {
        R<SysDeptDto> result = DEPT_SERVICE.getById(deptId);
        return validResult(result, null);
    }

    /**
     * 获取所有部门树
     *
     * @return 部门树
     */
    public static List<SysDeptDto> getDeptTree() {
        R<List<SysDeptDto>> result = DEPT_SERVICE.getAllTree();
        return validResult(result, Collections.emptyList());
    }

    /**
     * 获取某部门下所有部门(子部门)
     *
     * @param deptId 部门id
     * @return 部门集合
     */
    public static List<SysDeptDto> getChildDepts(String deptId) {
        R<List<SysDeptDto>> result = DEPT_SERVICE.getChildList(deptId);
        return validResult(result, Collections.emptyList());
    }

    /**
     * 根据某个部门查询上级部门
     *
     * @param deptId 部门id
     * @return 部门树
     */
    public static SysDeptDto getParentDept(String deptId) {
        R<SysDeptDto> result = DEPT_SERVICE.getParent(deptId);
        return validResult(result, null);
    }

    // 查询角色

    /**
     * 获取所有角色信息
     *
     * @return 角色信息集合
     */
    public static List<SysRoleDto> getAllRoles() {
        R<List<SysRoleDto>> result = ROLE_SERVICE.getAll();
        return validResult(result, Collections.emptyList());
    }

    /**
     * 根据角色id查询角色信息
     *
     * @param roleId 角色id
     * @return 角色信息集合
     */
    public static SysRoleDto getRoleById(String roleId) {
        R<SysRoleDto> result = ROLE_SERVICE.getById(roleId);
        return validResult(result, null);
    }

    /**
     * 根据角色id集合查询角色信息
     *
     * @param roleIds 角色id集合
     * @return 角色信息集合
     */
    public static List<SysRoleDto> getRolesByIds(List<String> roleIds) {
        R<List<SysRoleDto>> result = ROLE_SERVICE.getByIds(roleIds);
        return validResult(result, Collections.emptyList());
    }

    /**
     * 校验接口返回对象是否可用
     *
     * @param r 接口返回对象
     * @return 校验结果
     */
    private static <T> T validResult(R<T> r, T defaultValue) {
        if (Objects.isNull(r) || !r.is()) {
            return defaultValue;
        }
        return r.getData();
    }

}
