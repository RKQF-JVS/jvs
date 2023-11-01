package cn.bctools.auth.login.auth.wx.enterprise;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.enums.OtherLoginTypeEnum;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.web.utils.HttpRequestUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.UrlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: ZhuXiaoKang
 * @Description: 企业微信登录通用处理
 */
@Slf4j
@Component
public abstract class WxEnterpriseBase {
    public static final String LOGIN_TYPE = OtherLoginTypeEnum.WX_ENTERPRISE.name();

    /**
     * 企业微信相关属性
     */
    public static final String OPEN_ID = "openid";
    public static final String USER_ID = "userid";
    public static final String ERR_CODE = "errcode";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CONVERT_TO_OPENID_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid";

    @Resource
    UserService userService;
    @Resource
    UserRoleService userRoleService;
    @Resource
    UserExtensionService userExtensionService;

    public User getUser(String loginType, AuthUser authUser) {
        String nickname = authUser.getUsername();
        String openId = getOpenId(authUser);
        // 微信头像持久化存储
        String avatar = getDurableAvatar(nickname, authUser.getAvatar());
        // 企业微信自动注册登录
        UserExtension extension = userExtensionService.getOne(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getType, loginType)
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
                    .setType(loginType)
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

    /**
     * userid转openid
     * @param authUser 授权用户
     * @return openId
     */
    public String getOpenId(AuthUser authUser) {
        String openId = Optional.ofNullable(authUser.getRawUserInfo().get(OPEN_ID)).map(String::valueOf).orElse("");
        if (StringUtils.isNotBlank(openId)) {
            return openId;
        }
        // 未返回openid，则用userid转openid
        String url = UrlBuilder.fromBaseUrl(CONVERT_TO_OPENID_URL).queryParam(ACCESS_TOKEN, authUser.getToken().getAccessToken()).build();
        Map<String, Object> param = new HashMap<>();
        param.put(USER_ID,  String.valueOf(authUser.getRawUserInfo().get(USER_ID)));
        return getOpenId(url, param);
    }

    /**
     * userid转openid
     * @param accessToken
     * @param userId
     */
    public String getOpenId(String accessToken, String userId) {
        String url = UrlBuilder.fromBaseUrl(CONVERT_TO_OPENID_URL).queryParam(ACCESS_TOKEN, accessToken).build();
        Map<String, Object> param = new HashMap<>();
        param.put(USER_ID,  userId);
        return getOpenId(url, param);
    }

    /**
     *  userid转openid
     * @param url
     * @param param
     * @return
     */
    private String getOpenId(String url,  Map<String, Object> param) {
        JSONObject jsonObject = HttpRequestUtils.postJson(url, param, JSONObject.class, Boolean.FALSE, null);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new BusinessException("获取openId失败");
        }
        if (jsonObject.containsKey(ERR_CODE) && jsonObject.getIntValue(ERR_CODE) != 0) {
            log.error("企业微信登录获取openid失败：{}", jsonObject);
            throw new BusinessException("获取openId失败");
        }
        return jsonObject.getString(OPEN_ID);
    }

    abstract String getDurableAvatar(String nickname, String fileUrl);

    /**
     * 获取access_token
     * @return
     */
    public String getAccessToken() {
        String acessTokenUrl = UrlBuilder.fromBaseUrl(AuthDefaultSource.WECHAT_ENTERPRISE_WEB.accessToken())
                .queryParam("corpid", AuthConfigUtil.appId(SysConfigTypeEnum.WX_ENTERPRISE))
                .queryParam("corpsecret", AuthConfigUtil.secret(SysConfigTypeEnum.WX_ENTERPRISE))
                .build();
        JSONObject jsonObject = HttpRequestUtils.getJson(acessTokenUrl, JSONObject.class, Boolean.FALSE, null);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new BusinessException("获取access_token失败");
        }
        if (jsonObject.containsKey(ERR_CODE) && jsonObject.getIntValue(ERR_CODE) != 0) {
            log.error("企业微信登录获取access_token失败：{}", jsonObject);
            throw new BusinessException("获取access_token失败");
        }
        return jsonObject.getString(ACCESS_TOKEN);
    }
}
