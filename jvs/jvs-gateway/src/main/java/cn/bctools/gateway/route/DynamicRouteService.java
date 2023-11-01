package cn.bctools.gateway.route;

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.gateway.entity.GatewayRoutePo;
import cn.bctools.gateway.mapper.GatewayRouteMapper;
import cn.bctools.gateway.config.JvsMessageListenerAdapter;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Synchronized;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 动态刷新请求路由规则， 如果有变化默认2分钟会自动刷新配置网关路由
 *
 * @author guojing
 */
@Order(100)
@Component
public class DynamicRouteService extends SpringContextUtil implements ApplicationContextAware {

    @Resource
    GatewayProperties gatewayProperties;
    @Resource
    GatewayRouteMapper gatewayRouteMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        DiscoveryClient bean = applicationContext.getBean(DiscoveryClient.class);
        //获取数据库配置的路由规则
        //初始化获取数据库获取所有的路由
        List<GatewayRoutePo> gatewayRoutePos = gatewayRouteMapper.selectList(new LambdaQueryWrapper<GatewayRoutePo>());
        sync(bean, gatewayRoutePos);
    }

    /**
     * 通过监听redis 的key  gatewayRoute更新路由
     * @param bean
     * @return
     */
    @Bean
    JvsMessageListenerAdapter gatewayRoute(DiscoveryClient bean) {
        return new JvsMessageListenerAdapter() {
            @Override
            public void onMessage(String s) {
                //更新路由规则发生了改变
                List<GatewayRoutePo> gatewayRoutePos = JSONObject.parseArray(s, GatewayRoutePo.class);
                if (ObjectNull.isNotNull(gatewayRoutePos)) {
                    sync(bean, gatewayRoutePos);
                }
            }
        };
    }

    public static final String SERVICE_PATH = "lb://%s";
    public static final String MGR = "-mgr";
    public static final String BIZ = "-biz";
    public static List<RouteDefinition> sroutes = new ArrayList<>();

    @Synchronized
    private void sync(DiscoveryClient bean, List<GatewayRoutePo> gatewayRoutePos) {
        Map<String, RouteDefinition> dbRouteMap = getDbRoute(gatewayRoutePos);

        //获取Nacos配置的
        //只有Mgr 或biz的服务才能自动路由
        //获取所有服务
        Map<String, RouteDefinition> serviceRoute = getServiceRoute(bean);


        List<RouteDefinition> routes = gatewayProperties.getRoutes();
        for (int i = 0; i < routes.size(); i++) {
            //判断名称是否存在
            String id = routes.get(i).getId();
            if (serviceRoute.containsKey(id) && routes.get(i).equals(serviceRoute.get(id))) {
                //如果不同则直接加添一个值
                routes.set(i, serviceRoute.get(id));
                //删除map对象
                serviceRoute.remove(id);
            }
            if (dbRouteMap.containsKey(id) && routes.get(i).equals(dbRouteMap.get(id))) {
                //如果不同则直接加添一个值
                routes.set(i, dbRouteMap.get(id));
                //删除map对象
                dbRouteMap.remove(id);
            }
        }
        //执行两次为了保证数据库的在前面
        if (!dbRouteMap.isEmpty()) {
            //将所有的都添加到最前面
            routes.addAll(0, dbRouteMap.values());
        }
        if (!serviceRoute.isEmpty()) {
            //将所有的都添加到最前面
            routes.addAll(serviceRoute.values());
        }
        if (!sroutes.equals(routes)) {
            sroutes = routes;
            gatewayProperties.setRoutes(routes);
        }
    }

    private Map<String, RouteDefinition> getDbRoute(List<GatewayRoutePo> gatewayRoutePos) {
        return gatewayRoutePos
                .stream()
                .map(e -> {
                    try {
                        RouteDefinition routeDefinition = new RouteDefinition();
                        routeDefinition.setId(e.getId());
                        routeDefinition.setUri(new URI(e.getUri()));
                        Map<String, String> predicateParams = new HashMap<>(1);
                        PredicateDefinition predicate = new PredicateDefinition();
                        predicate.setName("Path");
                        predicateParams.put("pattern", e.getPath());
                        predicateParams.put("_genkey_0", e.getPath());
                        predicate.setArgs(predicateParams);
                        routeDefinition.setPredicates(Collections.singletonList(predicate));
                        //数据库配置优先于自动注册
                        return routeDefinition;
                    } catch (Exception uriSyntaxException) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(RouteDefinition::getId, Function.identity()));
    }

    private Map<String, RouteDefinition> getServiceRoute(DiscoveryClient bean) {
        List<String> services = bean.getServices().stream().filter(e -> e.endsWith(BIZ) || e.endsWith(MGR)).collect(Collectors.toList());
        return services.stream().map(e -> {
            RouteDefinition routeDefinition = new RouteDefinition();
            routeDefinition.setId(e);
            String format = String.format(SERVICE_PATH, e);
            try {
                routeDefinition.setUri(new URI(format));
                //定义第一个断言
                PredicateDefinition predicate = new PredicateDefinition();
                predicate.setName("Path");
                Map<String, String> predicateParams = new HashMap<>(1);
                String s = "";
                if (e.endsWith(MGR)) {
                    s = "/mgr/" + e.replaceAll(MGR, "") + "/**";
                } else if (e.endsWith(BIZ)) {
                    s = "/api/" + e.replaceAll(BIZ, "") + "/**";
                }
                predicateParams.put("pattern", s);
                predicateParams.put("_genkey_0", s);
                predicate.setArgs(predicateParams);
                routeDefinition.setPredicates(Collections.singletonList(predicate));
                return routeDefinition;
            } catch (Exception uriSyntaxException) {
                return null;
            }
        }).collect(Collectors.toMap(RouteDefinition::getId, Function.identity()));
    }

}
