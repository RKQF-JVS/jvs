package cn.bctools.oauth2.config;

import cn.hutool.core.util.ArrayUtil;
import cn.bctools.oauth2.prop.JvsOAuth2Property;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.openfeign.security.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
public class JvsAdapter extends ResourceServerConfigurerAdapter {

    RestTemplate restTemplate;
    JvsOAuth2Property jvsOAuth2Property;
    RemoteTokenServices remoteTokenServices;

    private static final String[] DEFAULT_PERMIT_URLS = {
            "/api/**",
            "/webjars/**",
            "/resources/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v2/**",
            "/doc.html",
            "index.html"};

    /**
     * 路由安全认证配置
     * <p>
     * 放行:
     * 1. 静态资源
     * 2. swagger相关接口
     * 3. 以"/api"开头的接口 (biz服务 与 mgr服务的feign接口)
     * 4. 配置的路径(jvs.oauth2.permitUrls)
     * <p>
     * 放行后的用户token:
     * |-------      |放行          |未放行
     * |有token      |有用户信息     |有用户信息
     * |无token      |无用户信息     |401异常
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        // 配置的放行路径
        List<String> permitUrls = jvsOAuth2Property.getPermitUrls();
        // 默认放行路径
        permitUrls.addAll(Arrays.asList(DEFAULT_PERMIT_URLS));
        String[] urls = ArrayUtil.toArray(permitUrls, String.class);
        http.authorizeRequests().antMatchers(urls)
                .permitAll()
                .anyRequest().authenticated().and().csrf().disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new JvsUserAuthenticationConverter();
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(defaultUserAuthenticationConverter);
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);

        //根据授权认证地址决定，可以是公网认证，或本地认证，如果内部集群环境则不需要此代码
        {   //公网认证
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
            interceptors.removeIf(e -> e instanceof LoadBalancerInterceptor);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setInterceptors(interceptors);
            restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
            remoteTokenServices.setRestTemplate(restTemplate);
        }
        {
            //如果内部集群环境
//            remoteTokenServices.setRestTemplate(restTemplate);
        }
        resources.tokenServices(remoteTokenServices);
    }

    /**
     * 微服务内部请求时将token放到请求头中
     * <p>
     * 增强失败时不作处理(无Token时)
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext,
                                                            OAuth2ProtectedResourceDetails resource) {
        return new OAuth2FeignRequestInterceptor(oAuth2ClientContext, resource) {
            @Override
            public void apply(RequestTemplate template) {
                try {
                    super.apply(template);
                } catch (Exception e) {
                    log.trace("用户权限增强失败: {}", e.getMessage());
                }
            }
        };
    }

}
