package cn.bctools.auth.component.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.bctools.auth.service.UserDetailsServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author glg
 */
@Getter
@Setter
public class OtherSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private AuthenticationEventPublisher defaultAuthenticationEventPublisher;

    @Autowired
    private AuthenticationSuccessHandler mobileLoginSuccessHandler;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(HttpSecurity http) {
        OtherAuthenticationFilter otherAuthenticationFilter = new OtherAuthenticationFilter();
        otherAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        otherAuthenticationFilter.setAuthenticationSuccessHandler(mobileLoginSuccessHandler);
        otherAuthenticationFilter.setEventPublisher(defaultAuthenticationEventPublisher);
        otherAuthenticationFilter.setAuthenticationEntryPoint(new ResourceAuthExceptionEntryPoint(objectMapper));
        OtherAuthenticationProvider mobileAuthenticationProvider = new OtherAuthenticationProvider();
        mobileAuthenticationProvider.setUserDetailsService(userDetailsService);
        http.authenticationProvider(mobileAuthenticationProvider).addFilterAfter(otherAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

    }

}
