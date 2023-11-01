package cn.bctools.gateway.filter;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.config.GatewayConfigProperties;
import cn.bctools.gateway.cons.GatewayCons;
import cn.bctools.gateway.service.CodeService;
import cn.bctools.gateway.utils.DataUtil;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
@Slf4j
@Component
public class GlobalRequestBodyDecodeFilter implements GlobalFilter, Ordered {

    private static final String MGR_URL = "/mgr";
    private static final String DOC_URL = "/v2/api-docs";
    private static final String KEY_404_PATH = "path";
    private static final String KEY_404_STATUS = "error";

    @Autowired
    CodeService codeService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    GatewayConfigProperties gatewayConfigProperties;
    static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        String clientIp = Objects.requireNonNull(remoteAddress).getAddress().getHostAddress();
        //如果是IP白名单，则直接返回
        if (gatewayConfigProperties.getIp().contains(clientIp)) {
            return chain.filter(exchange);
        }
        //如果开启了加密才做加密
        if (gatewayConfigProperties.isEncrypt() && path.startsWith(MGR_URL) && !path.endsWith(DOC_URL)) {
            List<Object> urls = redisUtils.lGet(GatewayCons.encode, 0, -1);
            if (ObjectNull.isNotNull(urls)) {
                for (Object url : urls) {
                    if (PATH_MATCHER.matchStart(String.valueOf(url), path)) {
                        //放开不加密
                        return chain.filter(exchange);
                    }
                }
            }
            // 对增/改操作，实现出参加密操作
            return operationExchange(exchange, chain);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    private Mono<Void> operationExchange(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType contentType = request.getHeaders().getContentType();
        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            NettyDataBufferFactory bufferFactory = (NettyDataBufferFactory) originalResponse.bufferFactory();

            @SuppressWarnings("unchecked")
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                Flux<DataBuffer> flux = null;
                if (body instanceof Mono) {
                    Mono<? extends DataBuffer> mono = (Mono<? extends DataBuffer>) body;
                    body = mono.flux();

                }
                if (body instanceof Flux) {
                    flux = (Flux<DataBuffer>) body;
                    return super.writeWith(flux.buffer().map(dataBuffers -> {

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        dataBuffers.forEach(i -> {
                            byte[] array = new byte[i.readableByteCount()];
                            i.read(array);
                            DataBufferUtils.release(i);
                            outputStream.write(array, 0, array.length);
                        });
                        //如果是特定的类型，直接不加密，直接返回数据
                        if (ObjectUtil.isNotEmpty(contentType) && contentType.toString().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                            return bufferFactory.wrap(outputStream.toByteArray());
                        }
                        try {
                            R r;
                            String result = outputStream.toString();
                            JSONObject jsonObject = JSONObject.parseObject(result, Feature.OrderedField);
                            if (HttpStatus.NOT_FOUND.getReasonPhrase().equals(jsonObject.getString(KEY_404_STATUS))) {
                                // 404
                                r = R.failed("404 Not Found : " + jsonObject.getString(KEY_404_PATH));
                            } else {
                                r = jsonObject.toJavaObject(R.class);
                            }
                            if (r.is()) {
                                if (ObjectUtil.isNotNull(r.getData())) {
                                    byte[] bytes = JSONObject.toJSONString(r.getData()).getBytes();
                                    String s = DataUtil.encodeBody(bytes);
                                    r.setData(s);
                                } else {
                                    r.setData(null);
                                }
                                return bufferFactory.wrap(JSONUtil.toJsonStr(r, JSONConfig.create().setIgnoreNullValue(false)).getBytes());
                            } else {
                                throw new BusinessException(r.getMsg());
                            }
                        } catch (BusinessException e) {
                            throw e;
                        } catch (Exception e) {
                            log.error("加密异常", e);
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    log.error("加密异常", e);
                                }
                            }
                        }
                        return bufferFactory.wrap(outputStream.toByteArray());
                    }));
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    public static void main(String[] args) {
        R<Object> objectR = new R<>();
        objectR.setData(new ArrayList<>());
        System.out.println(ObjectNull.isNull(objectR.getData()));
    }
}
