package cn.bctools.auth.config;

import cn.bctools.auth.service.ApplyService;
import cn.bctools.auth.service.UserDetailsServiceImpl;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.BeanToMapUtils;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证服务器配置
 *
 * @author gj
 */
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final ApplyService applyService;
    private final TokenStore tokenStore;

    static final String CREDENTIALS = "client_credentials";
    RedisUtils redisUtils;

    /**
     * 客户端要放在数据库中 直接使用数据库的官方客户端
     *
     * @param clients
     * @return
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(client -> {
            Apply apply = null;
            //直接查库
            if (redisUtils.hasKey(CREDENTIALS + client)) {
                apply = JSONObject.parseObject(String.valueOf(redisUtils.get(CREDENTIALS + client)), Apply.class);
            } else {
                apply = applyService.loadClientByClientId(client);
                if(ObjectNull.isNotNull(apply)) {
                    redisUtils.set(CREDENTIALS + client, JSONObject.toJSONString(apply), Long.valueOf(10 * 3000));
                }
            }
            BaseClientDetails details = new BaseClientDetails();
            details.setClientId(apply.getAppKey());
            details.setClientSecret(apply.getAppSecret());
            details.setScope(Collections.singleton("server"));
            details.setAuthorities(AuthorityUtils.createAuthorityList());
            details.setAuthorizedGrantTypes(apply.getAuthorizedGrantTypes());
            if (ObjectUtil.isNotEmpty(apply.getRegisteredRedirectUris())) {
                details.setRegisteredRedirectUri(apply.getRegisteredRedirectUris().stream().collect(Collectors.toSet()));
            }
            details.setAccessTokenValiditySeconds(apply.getAccessTokenValiditySeconds());
            details.setRefreshTokenValiditySeconds(apply.getRefreshTokenValiditySeconds());
            if (ObjectUtil.isNotEmpty(apply.getAdditionalInformation())) {
                details.setAdditionalInformation(apply.getAdditionalInformation());
            }
            details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(apply.getAutoApproveScopes()));
            return details;
        });
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                //如果是客户端校验就放开
                if (CREDENTIALS.equals(authentication.getOAuth2Request().getGrantType())) {
                    return accessToken;
                }
                Map<String, Object> stringObjectMap = BeanToMapUtils.beanToMap(authentication.getUserAuthentication().getPrincipal());
                //需要删除密码
                stringObjectMap.remove("password");
                UserDto userDto = (UserDto) stringObjectMap.get("userDto");
                userDto.setPassword(null);
                stringObjectMap.put("userDto", userDto);
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(stringObjectMap);
                return accessToken;
            }
        };
        endpoints.authenticationManager(authenticationManager)
                //配置加载用户信息的服务
                .userDetailsService(userDetailsService)
                .tokenStore(tokenStore)
                .exceptionTranslator(e -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
                    headers.set(HttpHeaders.PRAGMA, "no-cache");
                    if (e.getMessage().startsWith("Invalid refresh token")) {
                        return new ResponseEntity(R.failed(e.getMessage()).setCode(-3), headers, HttpStatus.OK);
                    }
                    return new ResponseEntity(R.failed(e.getMessage()), headers, HttpStatus.OK);
                })
                .tokenEnhancer(enhancerChain)
                // 刷新refresh_token缓存
                .reuseRefreshTokens(false);

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients().checkTokenAccess("permitAll()");
    }

}
