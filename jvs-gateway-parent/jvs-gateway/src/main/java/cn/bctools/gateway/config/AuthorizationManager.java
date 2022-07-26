package cn.bctools.gateway.config;

import cn.bctools.gateway.service.IgnorePathService;
import cn.bctools.gateway.service.PermissionService;
import cn.bctools.oauth2.dto.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Configuration
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Autowired
    IgnorePathService ignorePathService;
    @Autowired
    PermissionService permissionService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        // 1、从Redis中获取当前路径可访问角色列表
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String url = request.getPath().toString();
        boolean present = ignorePathService.getIgnorePath(url);
        //判断是否放开
        if (present) {
            return Mono.just(new AuthorizationDecision(true));
        }

        //根据URL找到对应的资源名称
        List<String> authorities = permissionService.getIgnorePath(url);
        return mono
                // 2、认证通过且角色匹配的用户可访问当前路径
                .flatMapIterable((e) -> ((CustomUser) e.getPrincipal()).getPermissions())
                .map(String::valueOf)
                .any((e) -> authorities.contains(e))
                .map((v) -> new AuthorizationDecision((Boolean) v))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
