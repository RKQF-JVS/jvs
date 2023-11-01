package cn.bctools.database.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author 
 */
@ConfigurationProperties("sql")
@Configuration
@Data
@RefreshScope
public class SqlProperties {

    /**
     * SQL 执行最大时长，毫秒，如果超长会有提示
     */
    private long maxTime = 200;

    /**
     * 是否开启性能分析
     * 默认不开启,默认开发环境开启
     */
    private boolean log = false;

    /**
     * SQL分析,将SQL拿来做explain解释器
     */
    private boolean explainIs = false;

    /**
     * 动态创建租户数据库,租户id动态路由开关
     */
    private boolean dynamicTenantDatabase = false;

}
