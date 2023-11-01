package cn.bctools.auth.contoller;

import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.enums.OAuthTypeEnum;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.AuthRequestCustomFactory;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.sms.config.AliSmsConfig;
import cn.bctools.web.utils.IpUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/just")
public class JustAuthController {

    private static final List<SysConfigTypeEnum> CONFIG_TYPES = Arrays.asList(SysConfigTypeEnum.LDAP);
    UserService userService;
    SmsEmailComponent smsComponent;
    AuthRequestCustomFactory factory;
    AliSmsConfig aliSmsConfig;
    DiscoveryClient discoveryClient;
    JustAuthProperties justAuthProperties;
    SysConfigService sysConfigService;

    /**
     * 返回可用的登录类型
     * <p>
     * 根据对应配置是否为空来判断
     *
     * @return 登录类型集合
     */
    @GetMapping
    public R<List<OAuthTypeEnum>> index(@RequestParam(value = "client_id") String clientId) {
        List<OAuthTypeEnum> loginTypes = new ArrayList<>();
        // 密码登录
        loginTypes.add(OAuthTypeEnum.password);
        // 短信登录
        if (!aliSmsConfig.isEmpty()) {
            loginTypes.add(OAuthTypeEnum.phone);
        }
        // 获取非扫码登录，且开启配置的登录类型
        List<SysConfigTypeEnum> enableTypes = sysConfigService.getEnableConfigType(clientId, CONFIG_TYPES);
        if (CollectionUtils.isNotEmpty(enableTypes)) {
            enableTypes.forEach(conf -> loginTypes.add(conf.getAuthType()));
        }
        //如果是移动端不返回
        if (!IpUtil.isMobile()) {
            // 获取开启了扫码登录的配置
            List<SysConfigTypeEnum> enableScanTypes = sysConfigService.getEnableScanConfigType(clientId);
            if (CollectionUtils.isNotEmpty(enableScanTypes)) {
                enableScanTypes.forEach(conf -> loginTypes.add(conf.getAuthType()));
            }
        }
        return R.ok(loginTypes);
    }

    /**
     * 获取登录配置
     * @param type 扫码登录类型
     * @return
     */
    @GetMapping("/config")
    public R<Map<String, Object>> getAuthConfig(OAuthTypeEnum type, @RequestParam(value = "client_id") String clientId) {
        return R.ok(sysConfigService.getConfig(clientId, SysConfigTypeEnum.getType(type), Boolean.TRUE));
    }

    /**
     * 登录
     * 对应授权系统的登录类型 {@linkplain LoginHandler}
     *
     * @param response response
     * @throws IOException
     */
    @RequestMapping("/login/{oauthType}")
    public void renderAuth(@PathVariable String oauthType, HttpServletResponse response, @RequestParam("url") String url, @RequestParam(value = "client_id") String clientId) throws IOException {
        AuthConfigUtil.setCurrentAppId(clientId);
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
