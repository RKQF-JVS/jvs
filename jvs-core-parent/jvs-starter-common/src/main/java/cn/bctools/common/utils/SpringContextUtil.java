package cn.bctools.common.utils;

import cn.hutool.core.io.FileUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.function.Get;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 服务上下文工具类，可直接操作此工具类获取版本号，服务名，环境和Spring 管理的Bean对象,在Aop、日志、组件重写等地方频繁使用
 *
 * @author guojing
 * @describe
 */
@EnableAsync
@Order(0)
@Component
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * 公共Spring上下文，默认为空，等项目启动后会进行初始化成功
     */
    protected static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * {@value applicationContextName} 直接获取系统的命名默认获取规则为 spring.application.name 方式，由于此配置默认使用@project.artifactId@  ,所以会直接使用pom中的项目名
     */
    @Getter
    protected static String applicationContextName = "";
    /**
     * 整个项目的密码key配置，默认使用jvs  如果需要自定义，需要前端配置修改配置
     */
    @Getter
    protected static String key = "jvs";
    /**
     * {@value env} 直接获取系统的命名默认获取规则为 spring.profiles.active 方式，由于此配置默认使用@profiles.active@  ,所以会直接使用打包的时候的环境,或由项目启动时指定 目前已经设置有dev|sit|uat|beta|pro五个环境，实际情况根据项目来
     */
    @Getter
    protected static String env = "";

    @Getter
    protected static String random = "";
    /**
     * {@value version} 直接获取系统的命名默认获取规则为 project.version 方式，由于此配置默认使用@project.version@  ,所以会直接使用pom中的项版本号
     * 业务系统版本号
     */
    @Getter
    protected static String version = "";

    /**
     * 公共工具，可直调用此方法直接获取任何Spring管理的Bean对象，可以获取Mapper  Service  Component Configuration等
     *
     * @param var Bean的Class
     * @author: guojing
     * @return: T 返回实体的Bean对象，并可直接调用其方法
     */
    public static <T> T getBean(Class<T> var) {
        return applicationContext.getBean(var);
    }

    /**
     * 重写Bean，主要为了初始化公共 {@linkplain ApplicationContext} 和初始化环境和名称对象
     *
     * @param context {@linkplain ApplicationContext}
     * @return: void
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        applicationContextName = context.getEnvironment().getProperty("spring.application.name");
        key = context.getEnvironment().getProperty("gateway.encryptKey");
        String port = context.getEnvironment().getProperty("server.port");
        String namespace = context.getEnvironment().getProperty("spring.cloud.nacos.discovery.namespace");
        String envformat = "[%s]-[%s]-[%s]";
        env = String.format(envformat, applicationContextName, port, namespace);
    }

    /**
     * 默认初始化{@linkplain RestTemplate}
     *
     * @return: org.springframework.web.client.RestTemplate
     */
    @Bean
    @Primary
    @LoadBalanced
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate lbRestTemplate() {
        // 获取上下文配置的ClientHttpRequestInterceptor 实现
        Map<String, ClientHttpRequestInterceptor> beansOfType = applicationContext
                .getBeansOfType(ClientHttpRequestInterceptor.class);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(new ArrayList<>(beansOfType.values()));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    /**
     * 添加元数据，自定义元数据
     */
    @Bean
    @ConditionalOnMissingBean
    public Void MetaData(NacosDiscoveryProperties nacosDiscoveryProperties, Environment environment) {
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        random = IdGenerator.getIdStr();
        version = metadata.getOrDefault(SysConstant.VERSION, "default");
        metadata.put("random", random);
        metadata.put("name", environment.getProperty("swagger.title"));
        try {
            Properties properties = System.getProperties();
            String s = properties.get("java.io.tmpdir").toString();
            String file = s.replaceAll("AppData\\\\Local\\\\Temp\\\\", "") + ".gitconfig";
            Optional<String> any = FileUtil.readUtf8Lines(file).stream().filter(e -> e.contains("=")).findAny();
            if (any.isPresent()) {
                String[] split = any.get().trim().split("=");
                version = split[1].trim();
                metadata.put(SysConstant.VERSION, version);
            } else {
                //设置默认版本号
                metadata.put(SysConstant.VERSION, SysConstant.DEFAULT);
            }
        } catch (Exception ignored) {
            //设置默认版本号
            metadata.put(SysConstant.VERSION, SysConstant.DEFAULT);
        }
        return null;
    }


}
