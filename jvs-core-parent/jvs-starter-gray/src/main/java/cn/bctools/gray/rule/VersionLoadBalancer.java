package cn.bctools.gray.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import cn.bctools.common.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
public class VersionLoadBalancer extends RoundRobinLoadBalancer {

    private final String serviceId;

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public VersionLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        super(serviceInstanceListSupplierProvider, serviceId);
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(serviceInstances -> getInstanceResponse(serviceInstances, request));
    }

    Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {

        // 服务注册中心无可用实例, 抛出异常
        if (CollUtil.isEmpty(instances)) {
            log.warn("No instance available. serviceId: {}", serviceId);
            return new EmptyResponse();
        }

        // 获取版本号, 获取异常时直接返回随机实例
        if (request == null || request.getContext() == null) {
            return new DefaultResponse(RandomUtil.randomEle(instances));
        }

        DefaultRequestContext requestContext = (DefaultRequestContext) request.getContext();
        if (!(requestContext.getClientRequest() instanceof RequestData)) {
            return new DefaultResponse(RandomUtil.randomEle(instances));
        }

        RequestData clientRequest = (RequestData) requestContext.getClientRequest();
        HttpHeaders headers = clientRequest.getHeaders();

        String reqVersion = headers.getFirst(SysConstant.VERSION);
        if (StrUtil.isNotBlank(reqVersion)) {
            reqVersion = URLDecoder.decode(reqVersion);
        } else {
            log.info("No version found");
            return new DefaultResponse(RandomUtil.randomEle(instances));
        }

        // 筛选元数据匹配的实例
        String finalReqVersion = reqVersion;
        List<ServiceInstance> matchedInstances = instances.stream().filter(instance -> {
            NacosServiceInstance nacosInstance = (NacosServiceInstance) instance;
            Map<String, String> metadata = nacosInstance.getMetadata();
            String targetVersion = MapUtil.getStr(metadata, SysConstant.VERSION, "-");
            return targetVersion.contains(finalReqVersion);
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(matchedInstances)) {
            // 存在, 则随机返回匹配的实例
            log.info("Found {} matched instance, returns a random one. serviceId: {}, version: {}", matchedInstances.size(), serviceId, reqVersion);
            return new DefaultResponse(RandomUtil.randomEle(matchedInstances));
        } else {
            // 不存在, 返回任意一个实例
            log.warn("No instance matched. serviceId: {}, version: {}", serviceId, reqVersion);
            return new DefaultResponse(RandomUtil.randomEle(instances));
        }
    }

}