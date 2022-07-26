package cn.bctools.sentinel.config;
import com.alibaba.cloud.sentinel.feign.SentinelFeign;
import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.common.utils.R;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Sentinel配置
 * @author Administrator
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class SentinelConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "feign.sentinel.enabled")
    public Feign.Builder feignSentinelBuilder() {
        return SentinelFeign.builder();
    }

    /**
     * 限流、熔断统一处理类
     */
    @Configuration
    @ConditionalOnClass(HttpServletRequest.class)
    public static class WebmvcHandler {
        @Bean
        public BlockExceptionHandler webmvcBlockExceptionHandler() {
            return (request, response, e) -> {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                R<Object> result = R.failed("Too many request, please retry later.");
                response.getWriter().print(JSONObject.toJSONString(result));
            };
        }
    }

}