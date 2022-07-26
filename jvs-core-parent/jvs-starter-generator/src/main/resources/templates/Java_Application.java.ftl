package ${rootPkg};

import cn.bctools.oauth2.annotation.EnableJvsMgrResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * <启动类>
 *
 * @author Auto Generator
 */
@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"${rootPkg}.*"})
@EnableJvsMgrResourceServer
public class ${upperCamelCase}MgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(${upperCamelCase}MgrApplication.class, args);
    }

}
