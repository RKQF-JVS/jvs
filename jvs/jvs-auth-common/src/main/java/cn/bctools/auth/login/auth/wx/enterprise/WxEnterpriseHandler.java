package cn.bctools.auth.login.auth.wx.enterprise;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.login.AuthRequestCustomFactory;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.enums.LoginTypeEnum;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.PasswordUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthWeChatEnterpriseQrcodeRequest;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhuXiaoKang
 * @Description: 企业微信扫码登录
 */
@Slf4j
@AllArgsConstructor
@Component("WECHAT_ENTERPRISE")
public class WxEnterpriseHandler extends WxEnterpriseBase implements LoginHandler<AuthCallback> {
    AuthRequestCustomFactory factory;

    @Override
    public User handle(String code, String appId, AuthCallback callback) {
        AuthWeChatEnterpriseQrcodeRequest authRequest = (AuthWeChatEnterpriseQrcodeRequest) factory.get(LoginTypeEnum.WECHAT_ENTERPRISE.getValue());
        AuthResponse response = authRequest.login(callback);
        if (!response.ok()) {
            log.error("企业微信扫码登录失败: {}", response.getMsg());
            throw new BusinessException("企业微信扫码登录失败");
        }
        log.info("[login] 获取企业微信登录信息: {}", JSONUtil.toJsonStr(response));
        AuthUser authUser = (AuthUser) response.getData();
        return getUser(LOGIN_TYPE, authUser);
    }

    @Override
    public void bind(User user, String code, String appId) {
        String decodedPassword = PasswordUtil.decodedPassword(code, appId);
        AuthCallback callback = JSONObject.parseObject(decodedPassword, AuthCallback.class);
        AuthWeChatEnterpriseQrcodeRequest authRequest = (AuthWeChatEnterpriseQrcodeRequest) factory.get(LoginTypeEnum.WECHAT_ENTERPRISE.getValue());
        AuthResponse response = authRequest.login(callback);
        log.info("[bind] 获取企业微信信息: {}", JSONUtil.toJsonStr(response));
        AuthUser authUser = (AuthUser) response.getData();
        String nickname = authUser.getNickname();
        String openId = getOpenId(authUser);
        // 企业微信用户头像持久化存储
        String avatar = getDurableAvatar(nickname, authUser.getAvatar());
        user.setHeadImg(avatar);
        UserExtension extension = userExtensionService.getOne(Wrappers.query(new UserExtension().setType(LOGIN_TYPE).setOpenId(openId)));
        // 判断是否重复绑定
        if (ObjectUtil.isNotEmpty(extension)) {
            throw new BusinessException("企业微信已绑定其它帐号");
        }
        // 绑定用户关键信息
        extension = new UserExtension()
                .setOpenId(openId)
                .setNickname(nickname)
                .setUserId(user.getId())
                .setType(LOGIN_TYPE)
                .setExtension(JSONObject.parseObject(JSONObject.toJSONString(authUser)));
        userService.updateById(user);
        userExtensionService.save(extension);
    }

    @Override
    public String getDurableAvatar(String nickname, String fileUrl) {
        return LoginHandler.super.getDurableAvatar(nickname, fileUrl);
    }
}
