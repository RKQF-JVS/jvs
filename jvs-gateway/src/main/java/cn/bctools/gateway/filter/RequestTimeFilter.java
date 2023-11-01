package cn.bctools.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.PRESERVE_HOST_HEADER_ATTRIBUTE;

/**
 * @author guojing
 */
@Slf4j
@Component
public class RequestTimeFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";
    private static final String INFO = "info";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String info = String.format("Path:{%s},Method:{%s},Host:{%s},Query:{%s}",
                exchange.getRequest().getURI().getPath(),
                Optional.ofNullable(exchange.getRequest().getMethod()).map(HttpMethod::name).orElse(""),
                exchange.getRequest().getURI().getHost(),
                exchange.getRequest().getQueryParams());
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        exchange.getAttributes().put(INFO, info);
        // 转发请求域名
        exchange.getAttributes().put(PRESERVE_HOST_HEADER_ATTRIBUTE, true);
        // 不是登录请求，直接向下执行
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            String infoStr = exchange.getAttribute(INFO);
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                log.info(infoStr + " 响应速度:" + executeTime + "ms");
            }
        }));
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
