package cn.bctools.web.config;

import cn.hutool.core.util.ObjectUtil;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.bctools.common.utils.TenantContextHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 利用重写Spring boot的拦截器实现，添加自定义的拦截并请请求中的TraceId和环境进行推送到日志中便于日志查询的排查
 *
 * @author My_gj
 */
@Slf4j
@Data
@Configuration
@ConditionalOnMissingBean(JvsWebMvcConfigurer.class)
public class JvsWebMvcConfigurer implements WebMvcConfigurer, ClientHttpRequestInterceptor {

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //指定拦截器类
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
                String header = request.getHeader(SysConstant.VERSION);
                String tenantId = request.getHeader(SysConstant.TENANTID);
                SystemThreadLocal.set(SysConstant.VERSION, header);
                //将租户放到当前线程中
                if (ObjectNull.isNull(TenantContextHolder.getTenantId())) {
                    TenantContextHolder.setTenantId(tenantId);
                }
                MDC.put("TraceId", TraceContext.traceId());
                MDC.put("env", SpringContextUtil.getEnv());
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                //将租户放到当前线程中
                String tenantId = request.getHeader(SysConstant.TENANTID);
                if (ObjectNull.isNull(TenantContextHolder.getTenantId())) {
                    TenantContextHolder.setTenantId(tenantId);
                }
                String header = request.getHeader(SysConstant.VERSION);
                SystemThreadLocal.set(SysConstant.VERSION, header);
                MDC.put("TraceId", TraceContext.traceId());
                MDC.put("env", SpringContextUtil.getEnv());
            }
        }).addPathPatterns("/**");
    }

    /**
     * 添加RestTemplate插件
     *
     * 将上下文中的版本号, 租户id放入请求头中
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        String version = SystemThreadLocal.get(SysConstant.VERSION);
        if (ObjectUtil.isNotEmpty(version)) {
            requestWrapper.getHeaders().add(SysConstant.VERSION, version);
        }
        String tenantId = SystemThreadLocal.get(SysConstant.TENANTID);
        if (ObjectUtil.isNotEmpty(tenantId)) {
            requestWrapper.getHeaders().add(SysConstant.TENANTID, tenantId);
        }
        return execution.execute(requestWrapper, body);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConvertorFactory());
    }
}
