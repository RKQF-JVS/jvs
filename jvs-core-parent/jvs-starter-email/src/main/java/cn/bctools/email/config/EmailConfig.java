package cn.bctools.email.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件配置
 *
 * @author: GuoZi
 */
@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(prefix = "jvs.email")
public class EmailConfig {

    /**
     * 发送配置的地址
     */
    String host = "smtp.qq.com";
    /**
     * 发送密码  发件人，现使用guojing的帐号
     */
    String pass = "";
    /**
     * 发送人
     */
    String from = "*****@qq.com";
    /**
     * 信息默认收件人，如果为空，则发送给系统默认收件人
     */
    List<String> to = new ArrayList<String>() {{
        add("*****@qq.com");
    }};

}
