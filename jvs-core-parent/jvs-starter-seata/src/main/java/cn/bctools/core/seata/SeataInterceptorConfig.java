package cn.bctools.core.seata;

import cn.bctools.core.seata.config.SeataConfiguration;
import feign.RequestInterceptor;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

/**
 * Feign请求头携带xid
 *
 * @Author: GuoZi
 */
@Slf4j
@Order
@Configuration
@Import(SeataConfiguration.class)
public class SeataInterceptorConfig {

    @Bean
    @Primary
    public RequestInterceptor seataInterceptorConfig() {
        return requestTemplate -> {
            // 传递分布式事务的xid
            requestTemplate.header(SeataXidFilter.SEATA_XID_KEY, RootContext.getXID());
        };
    }

}

