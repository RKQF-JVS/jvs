package cn.bctools.auth.wx;

import cn.bctools.auth.wx.handler.WxMpKeywordHandler;
import cn.bctools.auth.wx.handler.WxMpLoginHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
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
@Slf4j
public class WxMpConfiguration {
    private final WxMpLoginHandler wxMpLoginHandler;
    private final WxMpKeywordHandler wxMpKeywordHandler;

    @Bean
    public WxMpService wxMpService() {
        WxMpService service = new WxMpServiceImpl();
        Map<String, WxMpConfigStorage> configStorages = new HashMap<>();
        configStorages.put("", new WxMpDefaultConfigImpl());
        service.setMultiConfigStorages(configStorages);
        return service;
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        //客户回复关键字
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).handler(this.wxMpKeywordHandler).next();
        // 微信公众号登录
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).handler(this.wxMpLoginHandler).end();
        return newRouter;
    }

}
