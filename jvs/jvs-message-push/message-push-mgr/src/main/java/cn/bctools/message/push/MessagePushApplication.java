package cn.bctools.message.push;

import cn.bctools.oauth2.annotation.EnableJvsMgrResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication
@EnableJvsMgrResourceServer
public class MessagePushApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessagePushApplication.class, args);
    }
}
