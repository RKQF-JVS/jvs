package cn.bctools.auth.component;

import cn.bctools.common.utils.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author Administrator
 */
@Slf4j
@Component
@AllArgsConstructor
public class TenantDynamicDatasourceComponent {

    RestTemplate restTemplate;

    DiscoveryClient discoveryClient;

    /**
     * 初始化租户
     *
     * @param id 租户ID
     */
    public void init(String id) {
        discoveryClient.getServices()
                .stream()
                .map(discoveryClient::getInstances)
                .filter(ObjectUtils::isNotEmpty)
                .map(e -> e.stream().filter(v -> v.getMetadata().containsKey("tenantDynamicDatasource")).findAny().orElse(null))
                .filter(Objects::nonNull)
                .forEach(e -> {
                    String host = e.getHost();
                    try {
                        restTemplate.getForObject(host + "/api/tenantdynamic/datasource/tenant/" + id, R.class);
                    } catch (Exception restClientException) {
                        log.error("创建租户服务出错", restClientException);
                    }
                });
    }

}
