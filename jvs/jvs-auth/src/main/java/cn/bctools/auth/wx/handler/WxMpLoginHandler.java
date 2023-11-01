package cn.bctools.auth.wx.handler;

import cn.bctools.auth.entity.SysConfig;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.auth.wx.SysWxMpDto;
import cn.bctools.auth.wx.kefu.AsyncWxKeFuMessage;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author admin
 * [description]：登录
 */
@Slf4j
@Component
public class WxMpLoginHandler implements WxMpMessageHandler {
    private static final String EVENT_KEY_HEAD = "qrscene_";
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    SysConfigService sysConfigService;
    @Autowired
    AsyncWxKeFuMessage asyncWxKeFuMessage;
//    @Autowired
//    SysWxMpSettingsService sysWxMpSettingsService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        try {
            String tenantId = TenantContextHolder.getTenantId();
            Map<String, Object> collect = sysConfigService.list(Wrappers.query(new SysConfig().setJvsTenantId(tenantId).setType(SysConfigTypeEnum.valueOf("WECHAT_MP_MESSAGE"))))
                    .stream()
                    .collect(Collectors.toMap(SysConfig::getName, SysConfig::getContent));
            SysWxMpDto sysWxMpSettings = BeanUtil.mapToBean(collect, SysWxMpDto.class, false, CopyOptions.create());

//            SysWxMpSettings sysWxMpSettings = sysWxMpSettingsService.getOne(new LambdaQueryWrapper<>());
            WxMpUser wxmpUser = wxMpService.getUserService()
                    .userInfo(wxMessage.getFromUser(), null);
            //判断是否为登录
            String eventKey = wxMessage.getEventKey();
            if (StrUtil.isNotEmpty(eventKey)) {
                //如果是关注 微信会加上qrscene_
                eventKey = eventKey.replaceAll(EVENT_KEY_HEAD, "");
                boolean exists = redisUtils.exists(eventKey);
                if (exists) {
                    redisUtils.setExpire(eventKey, JSONObject.toJSONString(wxmpUser), 1, TimeUnit.HOURS);
                }
            }
            //判断是否为关注 关注才发送消息
            if (StrUtil.isNotEmpty(wxMessage.getEvent()) && wxMessage.getEvent().equals(WxConsts.EventType.SUBSCRIBE)) {
                //异步推送消息
                asyncWxKeFuMessage.asyncSetImageMessage(wxMpService, wxMessage.getFromUser());
                //推送关键字
                asyncWxKeFuMessage.asyncSetTextMessage(wxMpService, wxMessage.getFromUser(), sysWxMpSettings.getKeywordText());
                return WxMpXmlOutMessage.TEXT()
                        .content(sysWxMpSettings.getWelcomeText())
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser())
                        .build();
            }
        } catch (Exception e) {
            log.info("微信消息接收，登录判断错误!", e);
        }
        return null;
    }
}
