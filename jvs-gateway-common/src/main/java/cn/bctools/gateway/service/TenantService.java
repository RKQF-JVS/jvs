package cn.bctools.gateway.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.gateway.mapper.TenantMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 */
@Slf4j
@Service
@AllArgsConstructor
public class TenantService {
    TenantMapper tenantMapper;

    /**
     * 根据请求域名获取租户id
     *
     * @param loginDomain 登录域名
     * @return 租户id
     */
    public Optional<TenantPo> getTenantIdFromHost(String loginDomain) {
        TenantPo tenantPo = tenantMapper.selectOne(new LambdaQueryWrapper<TenantPo>().eq(TenantPo::getLoginDomain, loginDomain));
        if (ObjectUtils.isNotNull(tenantPo)) {
            //默认取第1个，其它的不要
            return Optional.ofNullable(tenantPo);
        }
        return Optional.empty();
    }

}
