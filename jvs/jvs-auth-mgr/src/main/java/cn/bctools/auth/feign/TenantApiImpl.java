package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.AuthTenantServiceApi;
import cn.bctools.auth.api.dto.SysTenantDto;
import cn.bctools.auth.service.TenantService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.entity.TenantPo;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "[Feign]租户信息接口")
public class TenantApiImpl implements AuthTenantServiceApi {

    private final TenantService tenantService;

    @Override
    public R<SysTenantDto> getById(String tenantId) {
        TenantPo tenantPo = Optional.ofNullable(tenantService.getById(tenantId)).orElseThrow(() -> new BusinessException("租户不存在"));
        return R.ok(BeanCopyUtil.copy(tenantPo, SysTenantDto.class));
    }
}
