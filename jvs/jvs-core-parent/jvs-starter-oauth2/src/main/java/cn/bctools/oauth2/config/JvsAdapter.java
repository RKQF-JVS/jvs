package cn.bctools.oauth2.config;

import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.oauth2.prop.JvsOAuth2Property;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

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
            "/license/**",
            "/frpc",
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
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> write(response))
                .accessDeniedHandler((request, response, accessDeniedException) -> write(response));
        http.authorizeRequests().antMatchers(urls)
                .permitAll()
                .anyRequest().authenticated().and().csrf().disable();
    }

    @SneakyThrows
    public void write(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSONObject.toJSONString(R.failed("没有权限").setCode(-2)));
        writer.flush();
        writer.close();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new JvsUserAuthenticationConverter();
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(defaultUserAuthenticationConverter);
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);
        //根据授权认证地址决定，可以是公网认证，或本地认证，如果内部集群环境则不需要此代码
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.removeIf(e -> e instanceof LoadBalancerInterceptor);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        remoteTokenServices.setRestTemplate(restTemplate);
        resources.tokenServices(remoteTokenServices);
    }

    /**
     * Feign请求增强, 用请求头传递用户Token
     * <p>
     * 增强失败时不作处理(无Token时)
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return template -> {
            try {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (Objects.nonNull(requestAttributes)) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    template.header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
                }
            } catch (Exception e) {
                log.trace("用户权限增强失败: {}", e.getMessage());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public Executor taskExecutor(ThreadPoolProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置线程池核心容量
        executor.setCorePoolSize(properties.getCorePoolSize());
        // 设置线程池最大容量
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        // 设置任务队列长度
        executor.setQueueCapacity(properties.getQueueCapacity());
        // 设置线程超时时间
        executor.setKeepAliveSeconds(properties.getKeepAliveTime());
        // 设置线程名称前缀
        executor.setThreadNamePrefix("jvs-executor-");
        // 设置任务丢弃后的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置任务的装饰
        executor.setTaskDecorator(runnable -> {
            RequestAttributes context = RequestContextHolder.currentRequestAttributes();
            SecurityContext authentication = SecurityContextHolder.getContext();
            String tenantId = TenantContextHolder.getTenantId();
            Authentication authenticationAuthentication = authentication.getAuthentication();
            Map<String, Object> systemThreadLocalMap = SystemThreadLocal.get();
            return () -> {
                SystemThreadLocal.setAll(systemThreadLocalMap);
                RequestContextHolder.setRequestAttributes(context);
                TenantContextHolder.setTenantId(tenantId);
                SecurityContextHolder.getContext().setAuthentication(authenticationAuthentication);
                runnable.run();
            };
        });
        executor.initialize();
        return executor;
    }


}
