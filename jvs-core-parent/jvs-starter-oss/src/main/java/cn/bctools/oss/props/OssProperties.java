package cn.bctools.oss.props;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 〈Alioss配置类〉
 *
 * @author auto
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * oss类型
     */
    private String name = "alioss";

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;

    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    /**
     * 公共桶，标识了公共桶获取的地址将是永久地址
     */
    private List<String> publicBuckets;
    /**
     * 过期小时 默认7天
     */
    private Integer timelinessHour = 7 * 24;
}
