package cn.bctools.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.config.GatewayConfigProperties;
import cn.bctools.gateway.service.CodeService;
import cn.bctools.gateway.utils.DataUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Administrator
 */
@Slf4j
@Component
public class GlobalRequestBodyDecodeFilter implements GlobalFilter, Ordered {

    private static final String MGR_URL = "/mgr";
    private static final String DOC_URL = "/v2/api-docs";

    @Autowired
    CodeService codeService;
    @Autowired
    GatewayConfigProperties gatewayConfigProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //如果开启了加密才做加密
        if (gatewayConfigProperties.isEncrypt() && path.startsWith(MGR_URL) && !path.endsWith(DOC_URL)) {
            // 对增/改操作，实现入参解密操作
            return operationExchange(exchange, chain);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @SneakyThrows
    public byte[] toByteArray(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = bos.toByteArray();
        oos.close();
        bos.close();
        return bytes;
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
                            String result = outputStream.toString();

                            R r = JSONObject.parseObject(result, R.class);
                            if (r.is()) {
                                byte[] bytes = JSONObject.toJSONString(r.getData()).getBytes();
                                String s = DataUtil.encodeBody(bytes);
                                r.setData(s);
                                return bufferFactory.wrap(JSONObject.toJSONString(r).getBytes());
                            }
                            codeService.transformCode(r);
                            //对返回code码进行二次转换,如果能找到的情况下
                            return bufferFactory.wrap(JSONObject.toJSONString(r).getBytes());
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

}
