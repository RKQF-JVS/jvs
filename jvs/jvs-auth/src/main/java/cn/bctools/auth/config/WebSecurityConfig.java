package cn.bctools.auth.config;

import cn.bctools.auth.component.other.OtherSecurityConfigurer;
import cn.bctools.auth.entity.LoginLog;
import cn.bctools.auth.handler.OtherLoginSuccessHandler;
import cn.bctools.auth.service.LoginLogService;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.oauth2.dto.CustomUser;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.time.LocalDateTime;

/**
 * SpringSecurity配置
 *
 * @author 
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/wx/portal/**");
        http.authorizeRequests()
                .antMatchers("/token/**", "/actuator/**", "/phone/**", "/just/**", "/wx/**", "/v2/**", "/api/domain").permitAll()
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

    @Bean
    public AuthenticationEventPublisher publisher(LoginLogService loginLogService) {
        return new AuthenticationEventPublisher() {
            @Override
            public void publishAuthenticationSuccess(Authentication authentication) {
                if (authentication.getPrincipal() instanceof CustomUser) {
                    save(((CustomUser) authentication.getPrincipal()).getUserDto(), true);
                }
            }

            private void save(UserDto userDto, boolean status) {
                //记录日志
                LoginLog copy = BeanCopyUtil.copy(userDto, LoginLog.class);
                copy.setId(null);
                copy.setOperateTime(LocalDateTime.now());
                copy.setStatus(status).setUserId(userDto.getId());
                copy.setTenantId(userDto.getTenant().getId());
                copy.setTenantShortName(userDto.getTenant().getShortName());
                copy.setTenantName(userDto.getTenant().getName());
                loginLogService.save(copy);
            }

            @Override
            public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
                if (authentication.getPrincipal() instanceof CustomUser) {
                    save(((CustomUser) authentication.getPrincipal()).getUserDto(), false);
                }
            }
        };
    }


}
