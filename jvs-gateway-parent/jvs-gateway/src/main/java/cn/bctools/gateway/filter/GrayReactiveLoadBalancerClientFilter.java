package cn.bctools.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.SpringContextUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author guojing
 */
@Slf4j
public class GrayReactiveLoadBalancerClientFilter extends ReactiveLoadBalancerClientFilter {

    static final String LOAD_BALANCE = "lb";

    GatewayLoadBalancerProperties loadBalancerProperties;
    DiscoveryClient discoveryClient;

    public GrayReactiveLoadBalancerClientFilter(GatewayLoadBalancerProperties properties, LoadBalancerProperties loadBalancerProperties, DiscoveryClient discoveryClient) {
        super(null, properties, loadBalancerProperties);
        this.loadBalancerProperties = properties;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!LOAD_BALANCE.equals(url.getScheme()) && !LOAD_BALANCE.equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        // preserve the original url
        ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
        }

        return choose(exchange).doOnNext(response -> {

            if (!response.hasServer()) {
                throw NotFoundException.create(loadBalancerProperties.isUse404(),
                        "Unable to find instance for " + url.getHost());
            }

            URI uri = exchange.getRequest().getURI();

            // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
            // if the loadbalancer doesn't provide one.
            String overrideScheme = null;
            if (schemePrefix != null) {
                overrideScheme = url.getScheme();
            }

            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(response.getServer(),
                    overrideScheme);

            URI requestUrl = LoadBalancerUriTools.reconstructURI(serviceInstance, uri);

            if (log.isTraceEnabled()) {
                log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
            }
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
        }).then(chain.filter(exchange));
    }

    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        ServiceInstance serviceInstance = choose(uri.getHost(), exchange.getRequest());
        return Mono.just(new DefaultResponse(serviceInstance));
    }

    /**
     * 根据serviceId筛选可用服务
     *
     * @param serviceId 服务ID
     * @param request   当前请求
     * @return 服务实例
     */
    @SneakyThrows
    public ServiceInstance choose(String serviceId, ServerHttpRequest request) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        // 注册中心无实例 抛出异常
        if (CollUtil.isEmpty(instances)) {
            log.warn("No instance available for {}", serviceId);
            throw new NotFoundException("No instance available for " + serviceId);
        }
        //获取名称
        String version = request.getHeaders().getFirst(SysConstant.VERSION);
        if (StringUtils.isNotBlank(version)) {
            version = URLDecoder.decode(version, "UTF-8");
        }
        //同网段随机找一个实例出来
        //获取当前启动的集群中的这个实例
        return defaultVersionOrRandomInstance(instances, version);

    }

    /**
     * 1、找是否有与网关相同的版本号,随机一个
     * 2、找是否有默认标识版本号
     * 3、随机找一个版本号
     *
     * @param instances
     * @param version
     * @return
     */
    private ServiceInstance defaultVersionOrRandomInstance(List<ServiceInstance> instances, String version) {
        List<ServiceInstance> collect = instances.stream().filter(instance -> MapUtil.getStr(instance.getMetadata(), SysConstant.VERSION, "-").contains(version))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            return collect.get(RandomUtil.randomInt(collect.size()));
        }
        //如果没有找到版本号，默认使用网关相同的版本号。
        ServiceInstance serviceInstance = discoveryClient.getInstances(SpringContextUtil.getApplicationContextName())
                .stream().filter(e -> e.getMetadata().containsValue(SpringContextUtil.getRandom()))
                .findFirst().get();

        //如果有版本号优先找和网关相同的版本
        String gatewayVersion = serviceInstance.getMetadata().get(SysConstant.VERSION);
        List<ServiceInstance> serviceInstances = instances.stream().filter(instance -> MapUtil.getStr(instance.getMetadata(), SysConstant.VERSION, "-").contains(gatewayVersion))
                .collect(Collectors.toList());

        if (!serviceInstances.isEmpty()) {
            return serviceInstances.get(RandomUtil.randomInt(serviceInstances.size()));
        }
        //找有没有默认版本号
        Optional<ServiceInstance> first = instances.stream().filter(e -> e.getMetadata().containsValue(SysConstant.DEFAULT)).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        //随机一个版本号
        return instances.get(RandomUtil.randomInt(instances.size()));
    }


}
