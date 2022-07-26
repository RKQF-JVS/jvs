package cn.bctools.feign.annotation;

import cn.bctools.feign.config.FeignEncodingConfig;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 组合型注解，可支持配置刷新和Feign客户端扫描
 *
 * @author guojing
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RefreshScope
@EnableFeignClients("cn.bctools.**.api")
@Import(FeignEncodingConfig.class)
public @interface EnableJvsFeignClients {

}
