package cn.bctools.dingding;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(prefix = "jvs.dingding")
public class DingDingConfig {
    /**
     * 密钥，机器人安全设置页面，加签一栏下面显示的SEC开头的字符串
     */
    String secret = "{密钥}";
    /**
     * 钉钉机器人的Webhook的地址
     */
    String url = "https://oapi.dingtalk.com/robot/send?access_token={Token}";
    /**
     * 需要 @哪些用户(手机号)，为空时默认 @全体成员
     */
    List<String> phones = Collections.emptyList();

}
