package cn.bctools.auth.service.impl;

import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.po.TenantUserData;
import cn.bctools.auth.mapper.UserTenantMapper;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class UserTenantServiceImpl extends ServiceImpl<UserTenantMapper, UserTenant> implements UserTenantService {

    @Override
    public void clearUser(@NotNull String userId) {
        this.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getCancelFlag, true).eq(UserTenant::getUserId, userId));
    }

    @Override
    public UserTenant checkUserId(String userId) {
        UserTenant userTenant = this.getOne(Wrappers.<UserTenant>lambdaQuery().eq(UserTenant::getUserId, userId));
        if (Objects.isNull(userTenant)) {
            throw new BusinessException("该用户不存在");
        }
        return userTenant;
    }

    @Override
    public IPage<TenantUserData> tenantUsers(Page page, Wrapper wrapper) {
        return baseMapper.tenantUsers(page, wrapper);
    }
}
