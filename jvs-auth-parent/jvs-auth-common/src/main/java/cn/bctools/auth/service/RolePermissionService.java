package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.Menu;
import cn.bctools.auth.entity.Permission;
import cn.bctools.auth.entity.RolePermission;

import java.util.List;

/**
 * 角色资源服务
 *
 * @author
 */
public interface RolePermissionService extends IService<RolePermission> {


    /**
     * 根据用户ID查询用户所属角色，并根据角色信息，查询用户的菜单资源权限。如果是超级管理员，则拥有所有的功能权限，和数据权限
     *
     * @param roleId 角色ID
     * @return
     */
    List<Menu> getMenuRoleById(List<String> roleId);


    /**
     * 根据用户ID获取功能权限结果集,
     * 根据当前用户，查询为数据权限的数据，然后查询数据表，sys_permission的权限数据，并返回给前端
     *
     * @param roleId 角色ID
     * @return 返回用户权限结果集
     */
    List<Permission> getFunctionRoleById(List<String> roleId);

}
