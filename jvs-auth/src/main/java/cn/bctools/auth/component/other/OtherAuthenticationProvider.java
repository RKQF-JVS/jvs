package cn.bctools.auth.component.other;

import cn.bctools.auth.service.UserDetailsServiceImpl;
import cn.bctools.common.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @author glg
 */
@Slf4j
public class OtherAuthenticationProvider implements AuthenticationProvider {

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private UserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Getter
    @Setter
    private UserDetailsServiceImpl userDetailsService;

    @Override
    @SneakyThrows
    public Authentication authenticate(Authentication authentication) {
        OtherAuthenticationToken otherToken = (OtherAuthenticationToken) authentication;

        UserDetails userDetails = userDetailsService.loadUserByOtherAuth(otherToken.getOtherParameter(), otherToken.getClientId());
        //获取用户信息
        if (userDetails == null) {
            log.debug("Authentication failed: no credentials provided");
            throw new BusinessException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.noopBindAccount", "Noop Bind Account"));
        }
        // 检查账号状态
        detailsChecker.check(userDetails);
        OtherAuthenticationToken authenticationToken = new OtherAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationToken.setDetails(otherToken.getDetails());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtherAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
