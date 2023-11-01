package cn.bctools.auth.api.api;

import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * 租户级配置信息
 *
 * @author 
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "tenantConfig")
public interface AuthTenantConfigServiceApi {


    String PREFIX = "/api/tenant/config";

    /**
     * 获取所有的应用信息
     */
    @GetMapping(PREFIX + "/key")
    R<Map<String, Object>> key(@RequestParam("key") String key, @RequestParam("clientId") String clientId, @RequestParam("tenantId") String tenantId);

}
