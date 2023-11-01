package cn.bctools.auth.config;

import cn.bctools.auth.constants.AuthConstant;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Administrator
 */
@Slf4j
@Component
public class TokenStoreConfig {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    TokenStore tokenStore(RedisConnectionFactory factory) {
        RedisTokenStore tokenStore = new RedisTokenStore(factory);
        tokenStore.setAuthenticationKeyGenerator(new DefaultAuthenticationKeyGenerator() {

            private static final String CLIENT_ID = "client_id";

            private static final String SCOPE = "scope";

            private static final String USERNAME = "username";

            private static final String USERAGENT = "userAgent";
            private static final String IP = "IP";

            @Override
            public String extractKey(OAuth2Authentication authentication) {
                Map<String, String> values = new LinkedHashMap<String, String>();
                OAuth2Request authorizationRequest = authentication.getOAuth2Request();
                if (!authentication.isClientOnly()) {
                    values.put(USERNAME, authentication.getName());
                }
                String appClientId = authorizationRequest.getRequestParameters().get(AuthConstant.APP_CLIENT_ID);
                values.put(CLIENT_ID, StringUtils.isBlank(appClientId) ? authorizationRequest.getClientId() : appClientId);
                if (authorizationRequest.getScope() != null) {
                    values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<String>(authorizationRequest.getScope())));
                }
                UserInfoDto principal = (UserInfoDto) authentication.getPrincipal();
                values.put(USERAGENT, principal.getUserDto().getUserAgent());
                values.put(IP, principal.getUserDto().getIp());
                return generateKey(values);
            }
        });
        tokenStore.setPrefix(SysConstant.JVS_AUTH);
        return tokenStore;
    }

}
