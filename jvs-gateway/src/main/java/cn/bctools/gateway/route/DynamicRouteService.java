package cn.bctools.gateway.route;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.gateway.entity.GatewayRoutePo;
import cn.bctools.gateway.mapper.GatewayRouteMapper;
import lombok.Synchronized;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
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

    static List<String> serviceNames = new ArrayList<>(10);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        DiscoveryClient bean = applicationContext.getBean(DiscoveryClient.class);
        //定时执行某个方法
        CronUtil.schedule("0/2 * * * * ?", (Task) () -> {
            sync(bean);
        });
        CronUtil.start(false);
        sync(bean);
    }

    public static final String SERVICE_PATH = "lb://%s";
    public static final String MGR = "-mgr";
    public static final String BIZ = "-biz";

    @Synchronized
    private void sync(DiscoveryClient bean) {
        //定时获取进行对比
        //获取所有服务
        //删除已经存在的
        List<String> services = bean.getServices().stream().filter(e -> e.endsWith(BIZ) || e.endsWith(MGR)).collect(Collectors.toList());
        services.removeAll(serviceNames);
        //获取Nacos配置的
        //只有Mgr 或biz的服务才能自动路由
        List<RouteDefinition> routes = gatewayProperties.getRoutes();
        services.forEach(e -> {
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
                routes.add(routeDefinition);
            } catch (Exception uriSyntaxException) {

            }
        });
        //加进去
        serviceNames.addAll(services);
        //排除已经放了的
        List<GatewayRoutePo> gatewayRoutePos = gatewayRouteMapper.selectList(new LambdaQueryWrapper<GatewayRoutePo>().notIn(ObjectUtil.isNotEmpty(serviceNames), GatewayRoutePo::getId, serviceNames));
        gatewayRoutePos.forEach(e -> {
            try {
                RouteDefinition routeDefinition = new RouteDefinition();
                routeDefinition.setId(e.getId());
                //加进去
                serviceNames.add(e.getId());
                routeDefinition.setUri(new URI(e.getUri()));
                Map<String, String> predicateParams = new HashMap<>(1);
                PredicateDefinition predicate = new PredicateDefinition();
                predicate.setName("Path");
                predicateParams.put("pattern", e.getPath());
                predicateParams.put("_genkey_0", e.getPath());
                predicate.setArgs(predicateParams);
                routeDefinition.setPredicates(Collections.singletonList(predicate));
                routes.add(routeDefinition);
            } catch (Exception uriSyntaxException) {

            }
        });
        gatewayProperties.setRoutes(routes);
    }

}
