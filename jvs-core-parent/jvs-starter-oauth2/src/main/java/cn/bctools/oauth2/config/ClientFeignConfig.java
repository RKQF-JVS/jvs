package cn.bctools.oauth2.config;

import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.hutool.core.codec.Base64Encoder;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

/**
 * @author Administrator
 */
@Slf4j
public class ClientFeignConfig {

    private static final String HEADER_PREFIX_AUTH = "Basic ";

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        if (clientId == null || clientSecret == null) {
            log.warn("Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.");
        }
        String auth = String.format("%s:%s", clientId, clientSecret);
        return HEADER_PREFIX_AUTH + Base64Encoder.encode(auth.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor clientRequestInterceptor(ResourceServerProperties clientSecret) {
        return (requestTemplate) -> {
            requestTemplate.header(SysConstant.TENANTID, String.valueOf(SystemThreadLocal.get(SysConstant.TENANTID)));
            requestTemplate.header(SysConstant.VERSION, String.valueOf(SystemThreadLocal.get(SysConstant.VERSION)));
            // 加请求头将凭证加到请求头上
            requestTemplate.header("Authorization", getAuthorizationHeader(clientSecret.getClientId(), clientSecret.getClientSecret()));
        };
    }

}
