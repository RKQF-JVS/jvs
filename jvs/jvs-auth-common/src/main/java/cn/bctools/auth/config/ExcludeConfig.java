package cn.bctools.auth.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: ZhuXiaoKang
 * @Description: 排除不需要的自动配置
 */
@Configuration
@EnableAutoConfiguration(exclude = {LdapAutoConfiguration.class})
public class ExcludeConfig {
}
