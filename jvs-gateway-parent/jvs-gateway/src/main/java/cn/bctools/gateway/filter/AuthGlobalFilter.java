package cn.bctools.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.gateway.service.TenantService;
import cn.bctools.gateway.utils.UrlUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.PRESERVE_HOST_HEADER_ATTRIBUTE;

/**
 * @author guojing
 */
@Slf4j
@Order(-1)
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    static final String OAUTH_TOKEN_URL = "/auth";
    static final String BIZ_URL = "/api";
    static final String MGR_URL = "/mgr";

    @Resource
    TenantService tenantService;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 转发请求域名
        exchange.getAttributes().put(PRESERVE_HOST_HEADER_ATTRIBUTE, true);
        // 获取请求信息
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String host = headers.getFirst("Referer");
        String version = headers.getFirst(SysConstant.VERSION);
        // 获取网关的版本号
        String gatewayVersion = SpringContextUtil.getVersion();
        // 使用请求头中的版本
        if (StringUtils.isBlank(version)) {
            if (StringUtils.isNotBlank(gatewayVersion)) {
                // 如果请求版本为空, 则使用网关的版本
                version = URLEncoder.encode(gatewayVersion, "UTF-8");
            } else {
                // 如果都为空, 则随机给一个版本号
                version = IdGenerator.getIdStr();
            }
        }
        Optional<TenantPo> tenant = tenantService.getTenantIdFromHost(host);
        String tenantId = tenant.map(TenantPo::getId).orElse("");
        log.info("tenant id from host: [{}] -> {}", host, tenantId);
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(SysConstant.TENANTID, tenantId)
                .header(SysConstant.VERSION, version)
                .build();
        exchange = exchange.mutate().request(request).build();
        // 不是登录请求，直接向下执行
        String path = request.getURI().getPath();
        // 截取"api/mgr"的请求路径
        // lb://jvs-auth-mgr/mgr/jvs-auth/xx -> lb://jvs-auth-mgr/xx
        if (path.startsWith(MGR_URL)) {
            exchange = UrlUtils.getServerWebExchange(exchange, exchange.getResponse(), 2);
        }
        if (path.startsWith(BIZ_URL)) {
            exchange = UrlUtils.getServerWebExchange(exchange, exchange.getResponse(), 2);
        }
        if (path.startsWith(OAUTH_TOKEN_URL)) {
            exchange = UrlUtils.getServerWebExchange(exchange, exchange.getResponse(), 1);
            URI uri = exchange.getRequest().getURI();
            String queryParam = uri.getRawQuery();
            Map<String, String> paramMap = HttpUtil.decodeParamMap(queryParam, Charset.defaultCharset());
            String password = paramMap.get("password");
            String clientId = paramMap.get("client_id");
            if (StrUtil.isNotBlank(password)) {
                return changeUrl(exchange, chain, uri, paramMap, password, clientId);
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 修改密码解密信息数据
     *
     * @param exchange
     * @param chain
     * @param uri
     * @param paramMap
     * @param clientId
     * @param password
     * @return
     */
    private Mono<Void> changeUrl(ServerWebExchange exchange, GatewayFilterChain chain, URI uri, Map<String, String> paramMap, String password, String clientId) {
        try {
            //appId为与前端对应的客户端名
            password = PasswordUtil.decodedPassword(password, clientId);
        } catch (Exception e) {
            log.error("密码解密失败:{}", password);
            return Mono.error(e);
        }
        paramMap.put("password", password.trim());
        URI newUri = UriComponentsBuilder.fromUri(uri)
                .replaceQuery(HttpUtil.toParams(paramMap))
                .build(true)
                .toUri();
        ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri).build();
        ServerWebExchange build = exchange.mutate().request(newRequest).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
