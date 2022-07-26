package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.UserTenant;

import javax.validation.constraints.NotNull;

/**
 * @author Administrator
 */
public interface UserTenantService extends IService<UserTenant> {

    /**
     * 移除指定用户信息
     *
     * @param userId 用户id
     */
    void clearUser(@NotNull String userId);

    /**
     * 校验用户是否存在
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserTenant checkUserId(String userId);

}
