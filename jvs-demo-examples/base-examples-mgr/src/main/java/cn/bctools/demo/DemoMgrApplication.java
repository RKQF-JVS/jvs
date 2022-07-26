package cn.bctools.demo;

import cn.bctools.oauth2.annotation.EnableJvsMgrResourceServer;
import com.xxl.job.core.config.annotation.EnableXxlJobExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author Administrator
 */
@EnableJvsMgrResourceServer
@EnableXxlJobExecutor
@EnableDiscoveryClient
@SpringBootApplication
public class DemoMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMgrApplication.class, args);
    }

}
