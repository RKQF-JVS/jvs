package cn.bctools.auth.config;

import cn.bctools.auth.component.other.OtherSecurityConfigurer;
import cn.bctools.auth.handler.OtherLoginSuccessHandler;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * SpringSecurity配置
 *
 * @author guojing
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/token/**", "/actuator/**", "/phone/**", "/just/**").permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint())
                .permitAll()
                .anyRequest()
                .authenticated()
                .and().apply(otherSecurityConfigurer());
    }

    @Bean
    public OtherSecurityConfigurer otherSecurityConfigurer() {
        return new OtherSecurityConfigurer();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationSuccessHandler mobileLoginSuccessHandler() {
        return new OtherLoginSuccessHandler();
    }

}
