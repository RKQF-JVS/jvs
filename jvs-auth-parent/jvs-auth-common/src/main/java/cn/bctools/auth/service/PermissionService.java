package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.Permission;

import java.util.List;

/**
 * @author
 */
public interface PermissionService extends IService<Permission> {


    /**
     * 根据用户ID查询所有的资源信息
     *
     * @param rolesIds 角色ID
     * @return
     */
    List<Permission> queryUserPermission(List<String> rolesIds);
}
