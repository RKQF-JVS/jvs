package cn.bctools.auth.service;

import cn.bctools.auth.entity.po.TenantUserData;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.UserTenant;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 用户列表
     * @param page 分页
     * @param wrapper 查询条件
     * @return 组织机构用户信息
     */
    IPage<TenantUserData> tenantUsers(Page page, @Param("ew") Wrapper wrapper);
}
