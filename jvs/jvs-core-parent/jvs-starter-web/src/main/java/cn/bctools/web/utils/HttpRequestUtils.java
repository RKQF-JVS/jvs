package cn.bctools.web.utils;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 * @Date: 2022/05/18 15:58
 */

import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SpringContextUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * http请求工具类
 *
 * @Author: GuoZi
 */
@Slf4j
@UtilityClass
public class HttpRequestUtils {

    /**
     * 请求路径前缀, 实际发送请求会优先使用 http://
     */
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static final String LOAD_BALANCE_PREFIX = "lb://";

    private static final RestTemplate REST_TEMPLATE;
    private static final DiscoveryClient DISCOVERY_CLIENT;

    static {
        REST_TEMPLATE = new RestTemplate();
        DISCOVERY_CLIENT = SpringContextUtil.getBean(DiscoveryClient.class);
    }

    /**
     * 发送GET请求
     * <p>
     * 响应数据结构为: {@link R<T>}
     * <p>
     * 请求方式: GET
     * 请求格式: application/json
     *
     * @param url          请求地址(可携带参数)
     * @param tClass       响应数据类型(R<T>)
     * @param asynchronous 是否异步发送
     * @return 响应数据, 异步时返回null
     */
    public static <T> T getJsonR(String url, Class<T> tClass, boolean asynchronous) {
        return parseBean(getJson(url, R.class, asynchronous, null), tClass);
    }

    /**
     * 发送请求
     * <p>
     * 响应数据结构为: {@link R<T>}
     * <p>
     * 请求方式: POST
     * 请求格式: application/json
     *
     * @param url          请求地址(可携带参数)
     * @param params       请求体参数
     * @param tClass       响应数据类型(R<T>)
     * @param asynchronous 是否异步发送
     * @return 响应数据, 异步时返回null
     */
    public static <T> T postJsonR(String url, Map<String, Object> params, Class<T> tClass, boolean asynchronous) {
        return parseBean(postJson(url, params, R.class, asynchronous, null), tClass);
    }

    /**
     * 发送GET请求
     * <p>
     * 响应数据结构为: {@link R<T>}
     * <p>
     * 请求方式: GET
     * 请求格式: application/json
     *
     * @param url          请求地址(可携带参数)
     * @param tClass       响应数据类型(R<T>)
     * @param asynchronous 是否异步发送
     * @param httpHeaders 请求头
     * @return 响应数据, 异步时返回null
     */
    public static <T> T getJsonR(String url, Class<T> tClass, boolean asynchronous, HttpHeaders httpHeaders) {
        return parseBean(getJson(url, R.class, asynchronous, httpHeaders), tClass);
    }

    /**
     * 发送请求
     * <p>
     * 响应数据结构为: {@link R<T>}
     * <p>
     * 请求方式: POST
     * 请求格式: application/json
     *
     * @param url          请求地址(可携带参数)
     * @param params       请求体参数
     * @param tClass       响应数据类型(R<T>)
     * @param asynchronous 是否异步发送
     * @param httpHeaders 请求头
     * @return 响应数据, 异步时返回null
     */
    public static <T> T postJsonR(String url, Map<String, Object> params, Class<T> tClass, boolean asynchronous, HttpHeaders httpHeaders) {
        return parseBean(postJson(url, params, R.class, asynchronous, httpHeaders), tClass);
    }

    /**
     * 发送请求
     * <p>
     * 请求方式: GET
     * 请求格式: application/json
     *
     * @param originalUrl  请求地址(可携带参数)
     * @param tClass       响应数据类型
     * @param asynchronous 是否异步发送
     * @param httpHeaders 请求头
     * @return 响应数据, 异步时返回null
     */
    public static <T> T getJson(String originalUrl, Class<T> tClass, boolean asynchronous, HttpHeaders httpHeaders) {
        final String url = handleUrl(originalUrl);
        // 构建请求体
        HttpHeaders headers = ObjectNull.isNull(httpHeaders) ? enhanceHttpHeader() : httpHeaders;
        final HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, headers);
        // 执行HTTP请求
        if (asynchronous) {
            ThreadPoolTaskExecutor executor = SpringContextUtil.getBean(ThreadPoolTaskExecutor.class);
            executor.execute(() -> REST_TEMPLATE.exchange(url, HttpMethod.GET, requestEntity, tClass));
            return null;
        }
        try {
            ResponseEntity<T> response = REST_TEMPLATE.exchange(url, HttpMethod.GET, requestEntity, tClass);
            // 获取请求返回值
            return response.getBody();
        } catch (Exception e) {
            log.error("http请求发送异常", e);
            throw new BusinessException("http请求发送异常: " + e.getMessage());
        }
    }

    /**
     * 发送请求
     * <p>
     * 请求方式: POST
     * 请求格式: application/json
     *
     * @param originalUrl  请求地址(可携带参数)
     * @param params       请求体参数
     * @param tClass       响应数据类型
     * @param asynchronous 是否异步发送
     * @param httpHeaders 请求头
     * @return 响应数据, 异步时返回null
     */
    public static <T> T postJson(String originalUrl, Map<String, Object> params, Class<T> tClass, boolean asynchronous, HttpHeaders httpHeaders) {
        final String url = handleUrl(originalUrl);
        // 构建请求体
        HttpHeaders headers = ObjectNull.isNull(httpHeaders) ? enhanceHttpHeader() : httpHeaders;
        final HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        // 执行HTTP请求
        if (asynchronous) {
            ThreadPoolTaskExecutor executor = SpringContextUtil.getBean(ThreadPoolTaskExecutor.class);
            executor.execute(() -> REST_TEMPLATE.exchange(url, HttpMethod.POST, requestEntity, tClass));
            return null;
        }
        try {
            ResponseEntity<T> response = REST_TEMPLATE.exchange(url, HttpMethod.POST, requestEntity, tClass);
            // 获取请求返回值
            return response.getBody();
        } catch (Exception e) {
            log.error("http请求发送异常", e);
            throw new BusinessException("http请求发送异常: " + e.getMessage());
        }
    }

    /**
     * 转换通用响应类
     * <p>
     * 返回R<T>类型时会丢失T的结构信息, 变成LinkedHashMap等
     *
     * @param r      通用响应数据
     * @param tClass 数据类型
     * @return 响应数据
     */
    private static <T> T parseBean(R<?> r, Class<T> tClass) {
        if (Objects.isNull(r)) {
            return null;
        }
        if (!r.is()) {
            throw new BusinessException(r.getMsg());
        }
        Object data = r.getData();
        if (data instanceof LinkedHashMap && !tClass.isAssignableFrom(LinkedHashMap.class)) {
            return BeanUtil.toBeanIgnoreError(data, tClass);
        }
        return (T) data;
    }

    /**
     * 请求头增强
     * <p>
     * 携带当前登录用户相关信息
     * 1. 应用服务名称
     * 2. 用户Token
     *
     * @return 请求头对象
     */
    @SneakyThrows
    private static HttpHeaders enhanceHttpHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置请求为json格式
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        // 设置应用信息
        String applicationContextName = SpringContextUtil.getApplicationContextName();
        httpHeaders.add(SysConstant.APPLICATION_NAME, applicationContextName);
        // 设置用户Token
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
        }
        return httpHeaders;
    }

    /**
     * 根据url前缀决定请求目标
     * <p>
     * 1. 根据服务名访问
     * 2. 根据域名或ip访问
     *
     * @param url 请求url
     * @return 请求对象
     */
    private String handleUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new BusinessException("请求地址为空");
        }
        // 替换服务名
        if (url.startsWith(LOAD_BALANCE_PREFIX)) {
            url = getLoadBalanceUrl(url);
        }
        // 添加url前缀
        if (!url.startsWith(HTTP_PREFIX) && !url.startsWith(HTTPS_PREFIX)) {
            url = HTTP_PREFIX + url;
        }
        return url;
    }

    /**
     * 服务名替换为ip:port
     * <p>
     * lb://服务名/** -> http://ip:port/**
     *
     * @param url 包含服务名的路径
     * @return http路径
     */
    private static String getLoadBalanceUrl(String url) {
        String sub = url.substring(LOAD_BALANCE_PREFIX.length());
        String[] split = sub.split("/", -1);
        String serviceId = split[0];
        String version = WebUtils.getRequest().getHeader(SysConstant.VERSION);
        ServiceInstance instance = ServiceRouteUtils.choose(DISCOVERY_CLIENT, serviceId, version);
        if (Objects.isNull(instance)) {
            throw new BusinessException("服务不存在: " + serviceId);
        }
        StringBuilder builder = new StringBuilder(HTTP_PREFIX);
        builder.append(instance.getHost()).append(":").append(instance.getPort());
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                builder.append('/').append(split[i]);
            }
        }
        return builder.toString();
    }

}
