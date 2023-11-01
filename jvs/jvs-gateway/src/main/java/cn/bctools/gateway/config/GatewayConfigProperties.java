package cn.bctools.gateway.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置返回数据是否加密，默认是加密的，只有mgr才加密,加密方式使用 ${@linkplain cn.bctools.common.utils.PasswordUtil} ,key 为 jvs 可在nacos上面配置服务
 *
 * @author Administrator
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties("gateway")
@EnableConfigurationProperties(LoadBalancerProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GatewayConfigProperties {

    private boolean encrypt = true;
    /**
     * ip白名单不加密
     */
    private List<String> ip = new ArrayList<>();

}
