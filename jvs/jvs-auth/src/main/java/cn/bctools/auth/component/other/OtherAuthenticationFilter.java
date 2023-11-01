package cn.bctools.auth.component.other;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author glg
 */
public class OtherAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 其它所有的登录
     */
    public static final String LOGIN_OTHER_AUTH = "/login";
    public static final String LOGIN_OTHER_AUTH_PARAMETER = "login_other_auth_parameter";

    @Getter
    @Setter
    private AuthenticationEventPublisher eventPublisher;
    @Getter
    @Setter
    private AuthenticationEntryPoint authenticationEntryPoint;

    public OtherAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/**", "GET"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals(HttpMethod.GET.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        AbstractAuthenticationToken abstractAuthenticationToken = getAbstractAuthenticationToken(request);
        setDetails(request, abstractAuthenticationToken);

        Authentication authResult = null;
        try {
            authResult = this.getAuthenticationManager().authenticate(abstractAuthenticationToken);
            logger.debug("Authentication success: " + authResult);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } catch (Exception exception) {
            try {
                authenticationEntryPoint.commence(request, response, new UsernameNotFoundException(exception.getMessage(), exception));
            } catch (Exception e) {
                logger.error("authenticationEntryPoint handle error:{}", exception);
            }
        }
        return authResult;
    }

    /**
     * 根据请求返回不同的抽象Token
     *
     * @param request
     * @return
     */
    private AbstractAuthenticationToken getAbstractAuthenticationToken(HttpServletRequest request) {
        if (request.getRequestURI().startsWith(LOGIN_OTHER_AUTH)) {
            String clientId = request.getParameter("client_id");
            return new OtherAuthenticationToken(request.getParameter(LOGIN_OTHER_AUTH_PARAMETER), clientId);
        }
        return null;
    }

    private void setDetails(HttpServletRequest request, AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
