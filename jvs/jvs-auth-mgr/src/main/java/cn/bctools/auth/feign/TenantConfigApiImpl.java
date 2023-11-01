package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.AuthTenantConfigServiceApi;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.service.ApplyService;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "租户级配置信息，根据key直接获取值")
public class TenantConfigApiImpl implements AuthTenantConfigServiceApi {

    ApplyService applyService;

    SysConfigService sysConfigService;

    @Override
    public R<Map<String, Object>> key(String key, String clientId, String tenantId) {
        Map<String, Object> key1 = sysConfigService.key(tenantId, clientId, SysConfigTypeEnum.valueOf(key));
        //返回这个key对应的值
        return R.ok(key1);
    }

}
