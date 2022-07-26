package cn.bctools.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.gateway.mapper.ApplyMapper;
import cn.bctools.gateway.service.TenantService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * 资源服务器配置
 *
 * @author guojing
 */
@AllArgsConstructor
@Configuration
public class ResourceServerConfig {
    AuthorizationManager authorizationManager;
    TokenStore tokenStore;
    TenantService tenantService;
    ApplyMapper applyMapper;
    NacosConfigManager nacosConfigManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        JvsReactiveAuthenticationManager tokenAuthenticationManager = new JvsReactiveAuthenticationManager(tokenStore);
        //认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(tokenAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        http.addFilterBefore(authenticationWebFilter, SecurityWebFiltersOrder.AUTHORIZATION);
        http
                .authorizeExchange()
                .pathMatchers("/static/**", "/agreement/**", "/auth/**", "/api/**", "/favicon.ico", ",/gateway/**", "/doc.html", "/webjars/**", "/swagger-resources/**").permitAll()
                .anyExchange()
                //因为放开地址在后台可以添加,所以和授权管理放在一起
                .access(authorizationManager)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((exchange, denied) -> Mono.error(() -> new InvalidTokenException("没有权限")))
                .and()
                .csrf().disable();
        return http.build();
    }

    /**
     * 登录前应用返回的域名 根据应用返回的域名直接将匹配规则
     */
    @Bean
    public RouterFunction routerFunction() {
        return RouterFunctions
                .route(RequestPredicates.path("/api/domain").and(RequestPredicates.accept(MediaType.ALL)),
                        request -> {
                            //根据域名查询应用,应用是否匹配,如果应用不匹配
                            //再查询租户是否存在
                            String host = request.uri().getHost();
                            String clientId = request.exchange().getRequest().getQueryParams().getFirst("client_id");
                            Object data = null;
                            if (StringUtils.isNotBlank(host)) {
                                data = tenantService.getTenantIdFromHost(host).orElseGet(() -> {
                                    Apply apply = applyMapper.selectOne(new LambdaQueryWrapper<Apply>().eq(Apply::getAppKey, clientId));
                                    if (ObjectNull.isNotNull(apply)) {
                                        return BeanCopyUtil.copy(apply, TenantPo.class);
                                    } else {
                                        return null;
                                    }
                                });
                            }
                            return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(JSONObject.toJSONString(R.ok(data)).getBytes()));
                        });
    }

    /**
     * 获取注册协议
     */
    @Bean
    public RouterFunction policiesFunction() {
        return RouterFunctions
                .route(RequestPredicates.path("/agreement/policies.html").and(RequestPredicates.accept(MediaType.ALL)),
                        request -> {
                            final String[] s = {""};
                            try {
                                s[0] = nacosConfigManager.getConfigService().getConfigAndSignListener("agreement", "DEFAULT_GROUP",
                                        3, new Listener() {
                                            @Override
                                            public Executor getExecutor() {
                                                return null;
                                            }

                                            @Override
                                            public void receiveConfigInfo(final String configInfo) {
                                                s[0] = configInfo;
                                            }
                                        });
                            } catch (NacosException e) {
                            }
                            return ServerResponse.status(HttpStatus.OK).contentType(MediaType.TEXT_HTML)
                                    .body(BodyInserters.fromValue(s[0].getBytes()));
                        });
    }

}
