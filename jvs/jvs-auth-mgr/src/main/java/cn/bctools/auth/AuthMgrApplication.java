package cn.bctools.auth;

import cn.bctools.auth.service.ConfigService;
import cn.bctools.oauth2.annotation.EnableJvsMgrResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author gj
 * 认证授权中心
 */
@EnableJvsMgrResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class AuthMgrApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AuthMgrApplication.class, args);
        //启动刷新配置
        ConfigService bean = run.getBean(ConfigService.class);
        bean.refresh();

    }

}
