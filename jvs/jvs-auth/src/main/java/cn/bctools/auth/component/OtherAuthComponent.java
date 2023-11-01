package cn.bctools.auth.component;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.auth.service.TenantService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.utils.PasswordUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
@Component
@AllArgsConstructor
public class OtherAuthComponent {

    UserService userService;
    TenantService tenantService;
    Map<String, LoginHandler> handlerMap;
    SysConfigService sysConfigService;

    /**
     * 根据标识获取用户
     *
     * @param type
     * @param code
     * @param appId
     * @return
     */
    public User getUser(String type, String code, String appId) {
        String decodedPassword = PasswordUtil.decodedPassword(code, appId);
        LoginHandler loginHandler = handlerMap.get(type);
        Object obj = JSONObject.parseObject(decodedPassword, getParameterClass(loginHandler.getClass()));
        AuthConfigUtil.setCurrentAppId(appId);
        return loginHandler.handle(code, appId, obj);
    }


    /**
     * 获取接口的泛型的类
     */
    public Class getParameterClass(Class<?> e) {
        Type obj = e.getGenericInterfaces()[0];
        ParameterizedType parameterizedType = (ParameterizedType) obj;
        return (Class) parameterizedType.getActualTypeArguments()[0];
    }

}
