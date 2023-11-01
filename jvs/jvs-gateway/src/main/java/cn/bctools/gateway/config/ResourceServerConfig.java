package cn.bctools.gateway.config;

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.mapper.ApplyMapper;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 资源服务器配置
 *
 * @author 
 */
@Configuration
public class ResourceServerConfig {
    @Autowired
    AuthorizationManager authorizationManager;
    @Autowired
    TokenStore tokenStore;
    @Autowired
    ApplyMapper applyMapper;
    @Autowired
    NacosConfigManager nacosConfigManager;
    private final static String NACOS_GROUP = "web_configuration";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        JvsReactiveAuthenticationManager tokenAuthenticationManager = new JvsReactiveAuthenticationManager(tokenStore);
        //认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(tokenAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        http.addFilterBefore(authenticationWebFilter, SecurityWebFiltersOrder.AUTHORIZATION);
        http
                .authorizeExchange()
                .pathMatchers("/icon/all/**", "/static/**", "/agreement/**", "/auth/**", "/im/**", "/get/nacos/config/**", "/api/**", "/favicon.ico", ",/gateway/**", "/doc.html", "/webjars/**",
                        "/swagger-resources/**").permitAll()
                .anyExchange()
                //因为放开地址在后台可以添加,所以和授权管理放在一起
                .access(authorizationManager)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((exchange, denied) -> Mono.error(() -> new InvalidTokenException("没有权限")))
                .and()
                .csrf().disable();
        return http.build();
    }

    /**
     * 获取配置文件
     */
    @Bean
    public RouterFunction policiesFunction() {
        return RouterFunctions
                .route(RequestPredicates.path("/get/nacos/config/**").and(RequestPredicates.accept(MediaType.ALL)),
                        request -> {
                            String[] split = request.path().split("/");
                            if (!(split.length == BigDecimal.ROUND_HALF_DOWN)) {
                                return null;
                            }
                            String dataId = split[4];
                            final String[] s = {""};
                            try {
                                s[0] = nacosConfigManager.getConfigService().getConfigAndSignListener(dataId, NACOS_GROUP,
                                        3, new Listener() {
                                            @Override
                                            public Executor getExecutor() {
                                                return null;
                                            }

                                            @Override
                                            public void receiveConfigInfo(final String configInfo) {
                                                s[0] = configInfo;
                                            }
                                        });
                            } catch (NacosException e) {
                            }
                            return ServerResponse.status(HttpStatus.OK).contentType(MediaType.TEXT_HTML)
                                    .body(BodyInserters.fromValue(JSONObject.toJSONString(JSONObject.parse(s[0])).getBytes()));
                        });
    }

    @Value("${iconPath:/icon/}")
    private String iconPath;

    /**
     * 获取icon
     */
    @Bean
    public RouterFunction iconFunction() {
        return RouterFunctions
                .route(RequestPredicates.path("/icon/all/**").and(RequestPredicates.accept(MediaType.ALL)),
                        request -> {
                            List<String> names = request.queryParams().getOrDefault("names[]", new ArrayList<>());

                            String path = request.path();
                            String header = "text/plain";
                            if (path.endsWith(".css")) {
                                header = "text/css";
                            }
                            if (path.endsWith(".js")) {
                                header = "text/javascript";
                            }
                            String[] split = path.split("/");
                            File file = FileUtil.file(iconPath);
                            if(!file.exists()){
                                return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8)
                                        .body(BodyInserters.fromValue(JSONObject.toJSONString(R.ok())));
                            }
                            byte[] bytes = "没有数据".getBytes();
                            if (split.length == 5) {
                                bytes = FileUtil.readBytes(new File(file.getPath() + "/" + URLDecoder.decode(split[3]) + "/" + split[4]));
                                return ServerResponse.status(HttpStatus.OK)
                                        .header("Content-Type", header)
                                        .body(BodyInserters.fromValue(bytes));
                            } else {
                                Map<String, File> fileMap = Arrays.stream(file.listFiles())
                                        .collect(Collectors.toMap(File::getName, Function.identity()));
                                List<Dict> collect = new ArrayList<>();
                                if (ObjectNull.isNotNull(names)) {
                                    //根据顺序获取数据
                                    collect = names.stream().filter(e -> fileMap.containsKey(e)).map(e -> getDict(fileMap.get(e))).collect(Collectors.toList());
                                } else {
                                    //自然顺序
                                    collect = fileMap.values().stream().sorted(Comparator.comparing(e -> e.getName())).map(e -> getDict(e)).collect(Collectors.toList());
                                }
                                bytes = new String(JSONObject.toJSONString(R.ok(collect)).getBytes(), Charset.defaultCharset()).getBytes();
                                return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8)
                                        .body(BodyInserters.fromValue(bytes));
                            }

                        });
    }

    private Dict getDict(File e) {
        Dict dict = new Dict();
        File jsFile = new File(iconPath + e.getName() + "/iconfont.js");
        if (jsFile.exists()) {
            dict.set("description", "/icon/all/" + e.getName() + "/iconfont.js");
        }
        return dict
                .set("value", "/icon/all/" + e.getName() + "/iconfont.css")
                .set("label", e.getName())
                .set("list", FileUtil.readLines(iconPath + e.getName() + "/iconfont.css", Charset.defaultCharset())
                        .stream()
                        .filter(v -> v.startsWith(".icon-") && v.contains(":"))
                        .map(v -> v.substring(1, v.indexOf(":")))
                        .collect(Collectors.toList())
                );
    }


}
