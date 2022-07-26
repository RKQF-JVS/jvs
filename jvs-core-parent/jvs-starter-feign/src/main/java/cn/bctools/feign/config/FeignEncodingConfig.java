package cn.bctools.feign.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.bctools.common.utils.TenantContextHolder;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author guojing
 */
@Slf4j
@Configuration
public class FeignEncodingConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    private static final String DEFAULT_TENANT_ID = "1";

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor jvsRequestInterceptor() {
        return (requestTemplate) -> {
            // 应用名称
            String applicationContextName = SpringContextUtil.getApplicationContextName();
            requestTemplate.header(SysConstant.APPLICATION_NAME, applicationContextName);
            // 实例版本号
            Object version = SystemThreadLocal.get(SysConstant.VERSION);
            if (ObjectUtil.isNotEmpty(version)) {
                requestTemplate.header(SysConstant.VERSION, (String.valueOf(version)));
            }
            // 租户id
            String tenantId = TenantContextHolder.getTenantId();
            if (StrUtil.isBlank(tenantId)) {
                tenantId = DEFAULT_TENANT_ID;
                log.trace("Empty tenant id in context, using default : " + tenantId);
            }
            requestTemplate.header(SysConstant.TENANTID, tenantId);
            log.trace("发送Feign请求: {}, 传递租户：{}, 传递token: {}", requestTemplate.path(), tenantId, requestTemplate.headers().get(HttpHeaders.AUTHORIZATION));
        };
    }

    /**
     * 自定义请求编码器，使用在FeignClient的微服务请求中发起的数据进行数据编码,目前还是使用默认的编码器
     *
     * @author: guojing
     * @return: feign.codec.Encoder
     */
    @Bean
    @Primary
    @Scope("prototype")
    @ConditionalOnMissingBean(Encoder.class)
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    /**
     * FeignClient请求的结果解码器，封装为如果返回结果为{@linkplain R} 则会自动判断R的返回状态，Code码是否为0(成功)  如果解码为R 并有错误信息，则直接抛出解码异常，会直接抛到接口调用层
     *
     * @author: guojing
     * @return: feign.codec.Decoder
     */
    @Bean
    @Primary
    @Scope("prototype")
    @ConditionalOnMissingBean(Decoder.class)
    public Decoder decoder() {
        return new SpringDecoder(messageConverters) {
            @Override
            public Object decode(Response response, Type type) throws IOException {
                //获取接口返回结果
                Object decode = super.decode(response, type);
                if (ObjectUtil.isNotEmpty(decode)) {
                    log.trace("解码后的结果为:{}", decode.toString());
                }
                if (decode instanceof R) {
                    R r = (R) decode;
                    //如果是成功，则直接返回
                    if (r.is()) {
                        log.trace("请求返回结果成功");
                        return decode;
                    } else {
                        log.error("FeignClient请求结果异常,返回结果为:{},错误信息为{}", JSONObject.toJSONString(decode), r.getMsg());
                        //如果是失败，直接抛异常处理
                        throw new BusinessException(r.getMsg(), r.getCode());
                    }
                }
                //拿到这个结果，判断，是否有正确的结果
                return decode;
            }
        };
    }

}
