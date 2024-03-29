package cn.bctools.auth.service.impl;

import cn.bctools.auth.service.TenantService;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.common.utils.TreeUtils;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.gateway.mapper.TenantMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 公司租户管理
 *
 * @author 
 */
@Slf4j
@Service
@AllArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, TenantPo> implements TenantService {

    TenantMapper tenantMapper;

    @Override
    public List<TenantPo> getChild() {
        String tenantId = TenantContextHolder.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return Collections.emptyList();
        }
        List<TenantPo> tenantList = this.list();
        return TreeUtils.getListPassingBy(tenantList, tenantId, TenantPo::getId, TenantPo::getParentId);
    }


    @Override
    public Optional<TenantPo> getTenantIdFromHost(String loginDomain, Set<String> tenantIds) {
        if (StringUtils.isBlank(loginDomain)) {
            return Optional.empty();
        }
        List<TenantPo> tenantPo = tenantMapper.selectList(new LambdaQueryWrapper<TenantPo>()
                .eq(TenantPo::getLoginDomain, loginDomain)
                .in(CollectionUtils.isNotEmpty(tenantIds), TenantPo::getId, tenantIds));
        if (ObjectUtils.isNotNull(tenantPo)) {
            //默认取第1个，其它的不要
            return Optional.ofNullable(tenantPo.get(0));
        }
        return Optional.empty();
    }


}
