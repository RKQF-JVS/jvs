package cn.bctools.oauth2.utils;

import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.SpringContextUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMessage;

import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 服务路由工具类
 *
 * @Author: GuoZi
 */
@Slf4j
public class ServiceRouteUtils {

    /**
     * 根据serviceId筛选可用服务
     *
     * @param discoveryClient 服务中心对象
     * @param serviceId       服务ID
     * @param request         当前请求
     * @return 服务实例
     */
    public static ServiceInstance choose(DiscoveryClient discoveryClient, String serviceId, HttpMessage request) {
        // 获取版本号
        String version = request.getHeaders().getFirst(SysConstant.VERSION);
        return choose(discoveryClient, serviceId, version);
    }

    /**
     * 根据serviceId筛选可用服务
     *
     * @param discoveryClient 服务中心对象
     * @param serviceId       服务id
     * @param version         版本号(路由标识)
     * @return 服务实例
     */
    @SneakyThrows
    public static ServiceInstance choose(DiscoveryClient discoveryClient, String serviceId, String version) {
        if (StringUtils.isNotBlank(version)) {
            version = URLDecoder.decode(version, "UTF-8");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        // 注册中心无实例, 抛出异常
        if (ObjectUtils.isEmpty(instances)) {
            log.warn("No instance available for {}", serviceId);
            throw new RuntimeException("No instance available for " + serviceId);
        }
        // 同网段随机找一个实例出来
        // 获取当前启动的集群中的这个实例
        return defaultVersionOrRandomInstance(discoveryClient, instances, version);
    }

    /**
     * 获取默认服务实例
     * <p>
     * 1、找是否有与网关相同的版本号,随机一个
     * 2、找是否有默认标识版本号
     * 3、随机找一个版本号
     *
     * @param instances 服务实例集合
     * @param version   版本号(路由标识)
     * @return 服务实例
     */
    private static ServiceInstance defaultVersionOrRandomInstance(DiscoveryClient discoveryClient, List<ServiceInstance> instances, String version) {
        List<ServiceInstance> matchedInstances = instances.stream()
                .filter(instance -> MapUtil.getStr(instance.getMetadata(), SysConstant.VERSION, "-").contains(version))
                .collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(matchedInstances)) {
            return matchedInstances.get(RandomUtil.randomInt(matchedInstances.size()));
        }
        //如果没有找到版本号，默认使用网关相同的版本号。
        ServiceInstance serviceInstance = discoveryClient.getInstances(SpringContextUtil.getApplicationContextName())
                .stream().filter(e -> e.getMetadata().containsValue(SpringContextUtil.getRandom()))
                .findFirst().get();
        //如果有版本号优先找和网关相同的版本
        String gatewayVersion = serviceInstance.getMetadata().get(SysConstant.VERSION);
        List<ServiceInstance> serviceInstances = instances.stream()
                .filter(instance -> MapUtil.getStr(instance.getMetadata(), SysConstant.VERSION, "-").contains(gatewayVersion))
                .collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(serviceInstances)) {
            return serviceInstances.get(RandomUtil.randomInt(serviceInstances.size()));
        }
        // 找有没有默认版本号
        Optional<ServiceInstance> first = instances.stream().filter(e -> e.getMetadata().containsValue(SysConstant.DEFAULT)).findFirst();
        // 随机一个版本号
        return first.orElseGet(() -> instances.get(RandomUtil.randomInt(instances.size())));
    }

}
