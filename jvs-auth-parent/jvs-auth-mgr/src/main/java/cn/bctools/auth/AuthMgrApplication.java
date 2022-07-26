package cn.bctools.auth;

import cn.bctools.oauth2.annotation.EnableJvsMgrResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author gj
 * 认证授权中心
 */
@EnableJvsMgrResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class AuthMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthMgrApplication.class, args);
    }

}
