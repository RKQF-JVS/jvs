package cn.bctools.message.push.feign;

import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.api.MessageConfigDetailApi;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.bctools.auth.api.api.AuthTenantConfigServiceApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "[Feign] 消息发送配置")
public class MessageConfigDetailApiImpl implements MessageConfigDetailApi {

    private final AuthTenantConfigServiceApi authTenantConfigServiceApi;

    @Override
    public R<Map<PlatformEnum,Boolean>> getEffectivePlatForm(){
        Map<PlatformEnum,Boolean> checkVo = new HashMap<>();
        checkVo.put(PlatformEnum.INSIDE_NOTIFICATION, Boolean.TRUE);
        checkVo.put(PlatformEnum.ALI_SMS, Optional.ofNullable(authTenantConfigServiceApi.key(SysConstant.SMS, SysConstant.FRAME,  TenantContextHolder.getTenantId()).getData()).isPresent());
        checkVo.put(PlatformEnum.EMAIL, Optional.ofNullable(authTenantConfigServiceApi.key(SysConstant.EMAIL, SysConstant.FRAME,  TenantContextHolder.getTenantId()).getData()).isPresent());
        checkVo.put(PlatformEnum.WECHAT_OFFICIAL_ACCOUNT, Optional.ofNullable(authTenantConfigServiceApi.key(SysConstant.WECHAT_MP, SysConstant.FRAME,  TenantContextHolder.getTenantId()).getData()).isPresent());
        return R.ok(checkVo);
    }
}
