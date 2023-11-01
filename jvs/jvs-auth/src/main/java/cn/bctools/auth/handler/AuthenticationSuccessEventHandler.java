package cn.bctools.auth.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author 
 */
@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationSuccessEventHandler extends AbstractAuthenticationSuccessEventHandler {


    /**
     * 处理登录成功方法
     * <p>
     * 获取到登录的authentication 对象
     *
     * @param authentication 登录对象
     */
    @Override
    public void handle(Authentication authentication) {
        log.info("用户：{} 登录成功", authentication.getPrincipal());
//        LoginLogComponent.successLog(authentication);
//        licenseHandler.checkLicense(authentication);
    }

}
