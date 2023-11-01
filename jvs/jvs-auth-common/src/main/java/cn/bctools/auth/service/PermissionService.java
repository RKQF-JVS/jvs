package cn.bctools.auth.service;

import cn.bctools.gateway.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 根据用户ID查询所有的资源信息
     *
     * @param appId    应用id
     * @param rolesIds 角色ID
     * @return 权限资源信息
     */
    List<Permission> queryUserPermission(String appId, List<String> rolesIds);

}
