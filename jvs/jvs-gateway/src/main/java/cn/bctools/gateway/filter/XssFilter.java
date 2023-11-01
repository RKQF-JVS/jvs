package cn.bctools.gateway.filter;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.gateway.cons.GatewayCons;
import cn.bctools.gateway.utils.XssUtil;
import cn.bctools.redis.utils.RedisUtils;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * XSS过滤
 *
 * @author lieber
 */
@Component
@Slf4j
@Data
public class XssFilter implements GlobalFilter, Ordered {

    @Autowired
    RedisUtils redisUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String method = request.getMethodValue();
        // 判断是否在白名单中
        if (this.white(uri.getPath())) {
            return chain.filter(exchange);
        }
        //内容传递请求
        if (ObjectNull.isNotNull(request.getHeaders().getContentType()) && request.getHeaders().getContentType().getType().equals(MediaType.MULTIPART_FORM_DATA.getType())) {
            return chain.filter(exchange);
        }
        // 只拦截POST和PUT请求
        if ((HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method))) {
            NettyDataBufferFactory factory = (NettyDataBufferFactory) exchange.getResponse().bufferFactory();
            return DataBufferUtils.join(request.getBody().defaultIfEmpty(factory.wrap(new EmptyByteBuf(factory.getByteBufAllocator()))))
                    .flatMap(dataBuffer -> {
                        // 取出body中的参数
                        byte[] oldBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(oldBytes);
                        String bodyString = new String(oldBytes, StandardCharsets.UTF_8);
                        int stlen = bodyString.length();

                        // 执行XSS清理
                        String bodyString2 = XssUtil.INSTANCE.cleanXss(bodyString);
                        if (bodyString2.length() != stlen) {
                            throw new BusinessException("请求被xxs拦截,请联系管理员");
                        }
                        ServerHttpRequest newRequest = request.mutate().uri(uri).build();

                        // 重新构造body
                        byte[] newBytes = bodyString.getBytes(StandardCharsets.UTF_8);
                        DataBuffer bodyDataBuffer = toDataBuffer(newBytes);
                        Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

                        // 重新构造header
                        HttpHeaders headers = new HttpHeaders();
                        headers.putAll(request.getHeaders());
                        // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
                        int length = newBytes.length;
                        headers.remove(HttpHeaders.CONTENT_LENGTH);
                        headers.setContentLength(length);
                        headers.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf8");
                        // 重写ServerHttpRequestDecorator，修改了body和header，重写getBody和getHeaders方法
                        newRequest = new ServerHttpRequestDecorator(newRequest) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return bodyFlux;
                            }

                            @Override
                            public HttpHeaders getHeaders() {
                                return headers;
                            }
                        };

                        return chain.filter(exchange.mutate().request(newRequest).build());
                    });
        } else {
            return chain.filter(exchange);
        }
    }

    static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 是否是白名单
     *
     * @param url 路由
     * @return true/false
     */
    private boolean white(String url) {
        List<Object> urls = redisUtils.lGet(GatewayCons.xss, 0, -1);
        if (ObjectNull.isNull(urls)) {
            return false;
        }
        return urls.stream().filter(e -> PATH_MATCHER.matchStart(String.valueOf(e), url)).count() > 0;
    }

    /**
     * 字节数组转DataBuffer
     *
     * @param bytes 字节数组
     * @return DataBuffer
     */
    private DataBuffer toDataBuffer(byte[] bytes) {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    public static final int ORDER = -1;

    @Override
    public int getOrder() {
        return ORDER;
    }


}
