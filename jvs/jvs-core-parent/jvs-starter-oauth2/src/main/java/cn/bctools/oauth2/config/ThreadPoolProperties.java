package cn.bctools.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolProperties {

    /**
     * 核心线程数（默认线程数）
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大线程数
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private int keepAliveTime = 60;
    /**
     * 缓冲队列大小
     */
    private int queueCapacity = 200;

}
