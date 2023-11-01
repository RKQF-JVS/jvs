package cn.bctools.auth.contoller;

import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.enums.OAuthTypeEnum;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.wx.WxMpProperties;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.sms.config.AliSmsConfig;
import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.xkcoding.justauth.AuthRequestFactory;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/just")
public class JustAuthController {

    UserService userService;
    SmsEmailComponent smsComponent;
    AuthRequestFactory factory;
    AliSmsConfig aliSmsConfig;
    DiscoveryClient discoveryClient;
    JustAuthProperties justAuthProperties;
    WxMpProperties wxMpProperties;

    /**
     * 返回可用的登录类型
     * <p>
     * 根据对应配置是否为空来判断
     *
     * @return 登录类型集合
     */
    @GetMapping
    public R<List<OAuthTypeEnum>> index() {
        List<OAuthTypeEnum> loginTypes = new ArrayList<>();
        // 密码登录
        loginTypes.add(OAuthTypeEnum.password);
        // 短信登录
        if (!aliSmsConfig.isEmpty()) {
            loginTypes.add(OAuthTypeEnum.phone);
        }
        // 微信登录
        AuthConfig authConfig = justAuthProperties.getType().get(AuthDefaultSource.WECHAT_OPEN.name());
        if (!this.isEmptyConfig(authConfig)) {
            loginTypes.add(OAuthTypeEnum.wx);
            return R.ok(loginTypes);
        }
        //微信公众号登录方式
        if (!wxMpProperties.isEmpty()) {
            loginTypes.add(OAuthTypeEnum.wxmp);
        }
        return R.ok(loginTypes);
    }

    /**
     * 登录
     * 对应授权系统的登录类型 {@linkplain LoginHandler}
     *
     * @param response response
     * @throws IOException
     */
    @RequestMapping("/login/{oauthType}")
    public void renderAuth(@PathVariable String oauthType, HttpServletResponse response, @RequestParam("url") String url) throws IOException {
        AuthRequest authRequest = factory.get(oauthType);
        Dict set = Dict.create()
                .set("url", url)
                .set("type", oauthType)
                .set("uid", IdGenerator.getIdStr());
        String state = PasswordUtil.encodePassword(JSONObject.toJSONString(set), SpringContextUtil.getKey());
        String authorize = authRequest.authorize(state);
        log.info("微信回调地址为:{}", authorize);
        response.sendRedirect(authorize);
    }

    @RequestMapping("/callback")
    public void callback(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) throws IOException {
        String decodedPassword = PasswordUtil.decodedPassword(state, SpringContextUtil.getKey());
        JSONObject jsonObject = JSONObject.parseObject(decodedPassword);
        jsonObject.put("code", code);
        jsonObject.put("state", state);
        String url1 = jsonObject.getString("url").replace("%23", "#");
        log.info("二次转发地址为:{}", url1);
        jsonObject.remove("url");
        jsonObject.remove("uid");
        String url = HttpUtil.urlWithForm(url1, jsonObject, Charset.defaultCharset(), true);
        response.sendRedirect(url);
    }

    /**
     * 判断登录配置是否为空
     *
     * @param config 配置内容
     * @return 判断结果
     */
    private boolean isEmptyConfig(AuthConfig config) {
        return Objects.isNull(config)
                || StringUtils.isBlank(config.getClientId())
                || StringUtils.isBlank(config.getClientSecret());
    }

}
