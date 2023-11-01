package cn.bctools.gateway.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * @author Administrator
 */
public class UrlUtils {

    private UrlUtils() {
    }

    /**
     * 请求头部清洗
     *
     * @param exchange
     * @param serverHttpResponseDecorator
     * @param i
     * @return
     */
    public static ServerWebExchange getServerWebExchange(ServerWebExchange exchange, ServerHttpResponse serverHttpResponseDecorator, int i) {
        // 1. 清洗请求头中from 参数
        ServerHttpRequest request = exchange.getRequest();
        String rawPath = request.getURI().getRawPath();
        // 2. 重写StripPrefix
        addOriginalRequestUrl(exchange, request.getURI());
        String newPath = "/" + Arrays.stream(org.springframework.util.StringUtils.tokenizeToStringArray(rawPath, "/")).skip(i).collect(Collectors.joining("/"));
        ServerHttpRequest serverHttpRequest = request.mutate().path(newPath).build();
        //重构请求服务网络交换器对象
        return exchange.mutate().request(serverHttpRequest)
                .response(serverHttpResponseDecorator).build();
    }

}
