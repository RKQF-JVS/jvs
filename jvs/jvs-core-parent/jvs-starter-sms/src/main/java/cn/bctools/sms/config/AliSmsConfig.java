package cn.bctools.sms.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

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

    public boolean isEmpty() {
        return StringUtils.isBlank(accessKeyId)
                || StringUtils.isBlank(accessKeySecret)
                || StringUtils.isBlank(signature)
                || Objects.isNull(template)
                || StringUtils.isBlank(template.getLogin());
    }

}
