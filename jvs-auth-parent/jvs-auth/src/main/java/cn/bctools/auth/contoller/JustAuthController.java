package cn.bctools.auth.contoller;

import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.SpringContextUtil;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/just")
public class JustAuthController {

    SmsEmailComponent smsComponent;
    UserService userService;
    AuthRequestFactory factory;

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

}
