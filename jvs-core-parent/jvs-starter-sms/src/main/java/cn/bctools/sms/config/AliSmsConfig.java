package cn.bctools.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 */
@Data
@Configuration
@ConfigurationProperties("sms")
public class AliSmsConfig {

    String accessKeyId;

    String accessKeySecret;
    /**
     * 签名
     */
    String signature;
    /**
     * 模板
     */
    Template template;

}
