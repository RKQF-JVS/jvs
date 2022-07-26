package cn.bctools.oauth2.config;

import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.SystemThreadLocal;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.codec.Base64;

import java.io.UnsupportedEncodingException;

/**
 * @author Administrator
 */
@Slf4j
public class ClientFeignConfig {

    private String getAuthorizationHeader(String clientId, String clientSecret) {

        if (clientId == null || clientSecret == null) {
            log.warn("Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.");
        }

        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor clientRequestInterceptor(ResourceServerProperties clientSecret) {
        return (requestTemplate) -> {
            requestTemplate.header(SysConstant.TENANTID, String.valueOf(SystemThreadLocal.get(SysConstant.TENANTID)));
            requestTemplate.header(SysConstant.VERSION, String.valueOf(SystemThreadLocal.get(SysConstant.VERSION)));
            //加请求头将凭证加到请求头上
            requestTemplate.header("Authorization", getAuthorizationHeader(clientSecret.getClientId(), clientSecret.getClientSecret()));
        };
    }

}
