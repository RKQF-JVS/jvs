package cn.bctools.gateway.swagger;

import cn.bctools.gateway.cons.SystemCons;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gj
 */

@AllArgsConstructor
public class SwaggerProvider implements SwaggerResourcesProvider {

    private static final String API_URI = "/v2/api-docs";

    private final RouteLocator routeLocator;
    private final GatewayProperties gatewayProperties;
    private final DiscoveryClient discoveryClient;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> services = discoveryClient.getServices();
        services.remove("jvs-gateway");

        List<String> routes = new ArrayList<>();
        //此处，不使用路由方式，而使用服务方式
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> services.contains(routeDefinition.getUri().getHost()) && routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> "Path".equalsIgnoreCase(predicateDefinition.getName()))
                        .forEach(predicateDefinition ->
                                resources.add(swaggerResource(routeDefinition.getId(),
                                        predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                                .replace("/**", API_URI)))));
        //因为有默认的服务请求方式，所以需要在这个地方添加其它的
        //过滤到已经匹配的数据
        List<String> route = resources.stream().map(SwaggerResource::getName).collect(Collectors.toList());
        //匹配自动的接口入口服务
        List<SwaggerResource> authRule = services.stream().filter(e -> !route.contains(e))
                .map(e -> {
                    SwaggerResource swaggerResource = new SwaggerResource();
                    swaggerResource.setName(e);
                    //如果是BIZ服务     /api/test  >>   test-biz     ||  /test     >> test-mgr
                    if (e.endsWith(SystemCons.BIZ)) {
                        swaggerResource.setLocation("/api/" + e.replaceAll(SystemCons.BIZ, "") + "/v2/api-docs");
                    } else if (e.endsWith(SystemCons.MGR)) {
                        swaggerResource.setLocation("/mgr/" + e.replaceAll(SystemCons.MGR, "") + "/v2/api-docs");
                    } else {
                        swaggerResource.setLocation(e + "/v2/api-docs");
                    }
                    swaggerResource.setSwaggerVersion("2.0");
                    return swaggerResource;
                }).collect(Collectors.toList());
        resources.addAll(authRule);
        return resources;
    }

    private static SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

}
