package cn.bctools.auth.login.auth.wx.enterprise;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.login.AuthRequestCustomFactory;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.auth.login.enums.LoginTypeEnum;
import cn.bctools.common.exception.BusinessException;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthWeChatEnterpriseWebRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description: 企业微信登录
 */
@Slf4j
@AllArgsConstructor
@Component("WECHAT_ENTERPRISE_WEB")
public class WxEnterpriseWebHandler extends WxEnterpriseBase implements LoginHandler<AuthCallback> {
    AuthRequestCustomFactory factory;
    WxEnterpriseDept deptHandler;
    WxEnterpriseUser userHandler;

    @Override
    public User handle(String code, String appId, AuthCallback callback) {
        AuthWeChatEnterpriseWebRequest authRequest = (AuthWeChatEnterpriseWebRequest) factory.get(LoginTypeEnum.WECHAT_ENTERPRISE_WEB.getValue());
        AuthResponse response = authRequest.login(callback);
        if (!response.ok()) {
            log.error("企业微信登录失败: {}", response.getMsg());
            throw new BusinessException("企业微信登录失败");
        }
        log.info("[login] 获取企业微信登录信息: {}", JSONUtil.toJsonStr(response));
        AuthUser authUser = (AuthUser) response.getData();
        return getUser(LOGIN_TYPE, authUser);
    }

    @Override
    public void bind(User user, String code, String appId) {

    }

    @Override
    public String getDurableAvatar(String nickname, String fileUrl) {
        return LoginHandler.super.getDurableAvatar(nickname, fileUrl);
    }

    @Override
    public List<Dept> getDeptAll() {
        String accessToken = getAccessToken();
        return deptHandler.getDeptAll(accessToken);
    }

    @Override
    public SyncUserDto getDeptUserAll(List<Dept> depts) {
        String accessToken = getAccessToken();
        return userHandler.getDeptUserAll(accessToken, depts);
    }
}
