package cn.bctools.auth.login;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.service.RoleService;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.oss.template.OssTemplate;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthWeChatOpenRequest;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Component("WECHAT_OPEN")
public class WxLoginHandler implements LoginHandler<AuthCallback> {

    OssTemplate ossTemplate;
    AuthRequestFactory factory;

    RoleService roleService;
    UserService userService;
    UserRoleService userRoleService;
    UserExtensionService userExtensionService;

    @Override
    public User handle(String code, String appId, AuthCallback callback) {
        AuthWeChatOpenRequest authRequest = (AuthWeChatOpenRequest) factory.get(AuthDefaultSource.WECHAT_OPEN.name());
        AuthResponse response = authRequest.login(callback);
        if (!response.ok()) {
            log.error("获取微信信息错误: {}", response.getMsg());
            throw new BusinessException("获取微信信息错误");
        }
        log.info("[login] 获取微信信息: {}", JSONUtil.toJsonStr(response));
        AuthUser authUser = (AuthUser) response.getData();
        String nickname = authUser.getNickname();

        String openId = authUser.getToken().getOpenId();
        // 微信头像持久化存储
        String avatar = getDurableAvatar(nickname, authUser.getAvatar());
        // 微信扫码自动注册登录
        UserExtension extension = userExtensionService.getOne(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getType, AuthDefaultSource.WECHAT_OPEN.name())
                .eq(UserExtension::getOpenId, openId));
        User user;
        if (ObjectUtil.isEmpty(extension)) {
            // 注册
            user = new User()
                    .setHeadImg(avatar)
                    .setRealName(nickname)
                    .setAccountName(IdGenerator.getIdStr(36))
                    .setCancelFlag(false);
            UserTenant userTenant = new UserTenant()
                    .setRealName(nickname)
                    .setCancelFlag(false);
            userService.saveUser(user, userTenant);
            UserExtension userExtension = new UserExtension()
                    .setExtension(BeanUtil.beanToMap(authUser))
                    .setOpenId(openId)
                    .setNickname(nickname)
                    .setType(AuthDefaultSource.WECHAT_OPEN.name())
                    .setUserId(user.getId());
            //设置为前端用户
            user.setUserType(UserTypeEnum.FRONT_USER);
            userExtensionService.save(userExtension);
            // 默认为游客角色
            userRoleService.grandDefaultSysRole(user.getId());
        } else {
            // 登录
            // 更新扩展数据
            extension.setExtension(BeanUtil.beanToMap(authUser));
            extension.setNickname(nickname);
            userExtensionService.updateById(extension);
            user = userService.getById(extension.getUserId());
            // 更新头像
            user.setHeadImg(avatar);
            userService.updateById(user);
        }
        return user;
    }

    @Override
    public void bind(User user, String code, String appId) {
        String decodedPassword = PasswordUtil.decodedPassword(code, appId);
        AuthCallback callback = JSONObject.parseObject(decodedPassword, AuthCallback.class);
        AuthWeChatOpenRequest authRequest = (AuthWeChatOpenRequest) factory.get(AuthDefaultSource.WECHAT_OPEN.name());
        AuthResponse response = authRequest.login(callback);
        log.info("[bind] 获取微信信息: {}", JSONUtil.toJsonStr(response));
        AuthUser authUser = (AuthUser) response.getData();
        String nickname = authUser.getNickname();
        String openId = authUser.getToken().getOpenId();
        // 微信头像持久化存储
        String avatar = getDurableAvatar(nickname, authUser.getAvatar());
        user.setHeadImg(avatar);
        UserExtension extension = userExtensionService.getOne(Wrappers.query(new UserExtension().setType(AuthDefaultSource.WECHAT_OPEN.getName()).setOpenId(openId)));
        // 判断是否重复绑定
        if (ObjectUtil.isNotEmpty(extension)) {
            throw new BusinessException("微信已绑定其它帐号");
        }
        // 绑定用户关键信息
        extension = new UserExtension()
                .setOpenId(openId)
                .setNickname(nickname)
                .setUserId(user.getId())
                .setType(AuthDefaultSource.WECHAT_OPEN.name())
                .setExtension(JSONObject.parseObject(JSONObject.toJSONString(authUser)));
        userService.updateById(user);
        userExtensionService.save(extension);
    }
}
