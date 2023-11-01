package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.UserGroup;

import javax.validation.constraints.NotNull;

/**
 * 用户组
 *
 * @author auto
 */
public interface UserGroupService extends IService<UserGroup> {

    /**
     * 移除指定用户信息
     *
     * @param userId 用户id
     */
    void clearUser(@NotNull String userId);

}
