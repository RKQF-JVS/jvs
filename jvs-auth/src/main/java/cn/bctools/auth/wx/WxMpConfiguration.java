package cn.bctools.auth.wx;

import cn.bctools.auth.wx.handler.WxMpLoginHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * wechat mp configuration
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
@Slf4j
public class WxMpConfiguration {
    private final WxMpProperties properties;
    private final WxMpLoginHandler wxMpLoginHandler;

    @Bean
    public WxMpService wxMpService() {
        if (properties.isEmpty()) {
            log.error("微信公众号,账户信息未配置!");
            return new WxMpServiceImpl();
        }

        WxMpService service = new WxMpServiceImpl();
        Map<String, WxMpConfigStorage> configStorages = new HashMap<>(1);
        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        wxMpConfigStorage.setAppId(properties.getAppId());
        wxMpConfigStorage.setAesKey(properties.getAesKey());
        wxMpConfigStorage.setSecret(properties.getSecret());
        wxMpConfigStorage.setToken(properties.getToken());
        configStorages.put(properties.getAppId(), wxMpConfigStorage);
        service.setMultiConfigStorages(configStorages);
        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        // 微信公众号登录
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).handler(this.wxMpLoginHandler).end();
        return newRouter;
    }

}
