package cn.bctools.gateway.config;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.StackTraceElementUtils;
import cn.bctools.gateway.entity.GatewayCodePo;
import cn.bctools.gateway.mapper.GatewayCodeMapper;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 统一异常处理
 *
 * @author 
 */
@Slf4j
@Configuration
@Primary
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class GatewayErrorConfig implements ErrorWebExceptionHandler {

    GatewayCodeMapper gatewayCodeMapper;
    RedisUtils redisUtils;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // header set
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }
        String message = ex.getMessage();
        R r = R.failed(message);
        if (ex instanceof BusinessException) {
            //转义
            GatewayCodePo gatewayCodePo = gatewayCodeMapper.selectOne(Wrappers.query(new GatewayCodePo().setMsg(message)));
            if (ObjectUtil.isNotEmpty(gatewayCodePo)) {
                //如果有就替换，如果 没有就还是使用默认的
                r.setCode(gatewayCodePo.getCode());
            }
        } else if (ex instanceof FlowException) {
            r = R.failed("接口限流了");
        } else if (ex instanceof DegradeException) {
            r = R.failed("服务降级了");
        } else if (ex instanceof ParamFlowException) {
            r = R.failed("热点参数限流了");
        } else if (ex instanceof SystemBlockException) {
            r = R.failed("触发系统保护规则");
        } else if (ex instanceof AuthorityException) {
            r = R.failed("触发系统保护规则");
        } else if (ObjectUtil.isNotEmpty(message)) {
            //转义
            GatewayCodePo gatewayCodePo = gatewayCodeMapper.selectOne(Wrappers.query(new GatewayCodePo().setMsg(message)));
            if (ObjectUtil.isNotEmpty(gatewayCodePo)) {
                r.setCode(gatewayCodePo.getCode());
            } else {
                r.setCode(500);
                r.setMsg("未知错误");
                //生成错误ID
                r.setData(IdGenerator.getIdStr());
            }
        }

        String body = JSONObject.toJSONStringWithDateFormat(r, "yyyy-MM-dd HH:mm:ss SSS", SerializerFeature.NotWriteDefaultValue);
        //转义
        String s = StackTraceElementUtils.logThrowableToString(ex);
        log.error("网关错误拦截:返回信息:{},异常信息为:{}", body, s);
        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    return bufferFactory.wrap(body.getBytes());
                }));
    }

}
