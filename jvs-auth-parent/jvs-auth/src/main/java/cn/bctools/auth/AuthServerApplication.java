package cn.bctools.auth;

import cn.bctools.feign.annotation.EnableJvsFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author gj
 * 认证授权中心
 */
@EnableJvsFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

}
