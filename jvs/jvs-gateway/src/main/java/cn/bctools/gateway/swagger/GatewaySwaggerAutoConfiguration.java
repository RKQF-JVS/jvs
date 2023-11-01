package cn.bctools.gateway.swagger;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Administrator
 */
@EnableSwagger2
@Configuration
public class GatewaySwaggerAutoConfiguration {

    @Bean
    @Primary
    public SwaggerProvider swaggerProvider(RouteLocator routeLocator, GatewayProperties gatewayProperties,
                                           DiscoveryClient discoveryClient) {
        return new SwaggerProvider(routeLocator, gatewayProperties, discoveryClient);
    }

}
