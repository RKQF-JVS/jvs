package cn.bctools.auth.handler;

import cn.bctools.oauth2.dto.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

/**
 * @author 
 */
@Slf4j
public abstract class AbstractAuthenticationSuccessEventHandler
        implements ApplicationListener<AuthenticationSuccessEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String className = "org.springframework.security.authentication.UsernamePasswordAuthenticationToken";
        //这里的事件源除了登录事件（UsernamePasswordAuthenticationToken）
        //还有可能是token验证事件源（OAuth2Authentication）
        //排除登录验证事件
        if (event.getSource().getClass().getName().equals(className)) {
            if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken source = (UsernamePasswordAuthenticationToken) event.getSource();
                Object principal = source.getPrincipal();
                //认证成功会 返回一个CustomUser对象
                if (principal instanceof CustomUser) {
//                    log.info("登录成功");
//                    handle(source);
                }
            }
        }
    }

    /**
     * 处理登录成功方法
     * <p>
     * 获取到登录的authentication 对象
     *
     * @param authentication 登录对象
     */
    public abstract void handle(Authentication authentication);

}
