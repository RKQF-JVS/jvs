package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserRole;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户角色
 *
 * @author
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 根据用户id查询角色信息集合
     *
     * @param userId 用户ID集
     * @return 角色信息集合
     */
    List<Role> getRoleByUserId(String userId);

    /**
     * 根据角色id查询用户信息集合
     *
     * @param roleId 角色id
     * @return 用户信息集合
     */
    List<User> getUsersByRoleId(String roleId);

    /**
     * 赋予指定用户默认的系统角色
     *
     * @param userId 用户id
     */
    void grandDefaultSysRole(String userId);

    /**
     * 移除指定用户信息
     *
     * @param userId 用户id
     */
    void clearUser(@NotNull String userId);

}
