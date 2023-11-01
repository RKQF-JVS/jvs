package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.Role;

/**
 * 角色服务
 *
 * @author
 */
public interface RoleService extends IService<Role> {

    /**
     * 创建系统预留角色
     */
    void createDefaultSysRole();

}
