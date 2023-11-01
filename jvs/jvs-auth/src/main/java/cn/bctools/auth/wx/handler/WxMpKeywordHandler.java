package cn.bctools.auth.wx.handler;

//import cn.bctools.auth.entity.SysWxMpSettings;

import cn.bctools.auth.entity.SysConfig;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.entity.po.WxKeywordData;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.auth.wx.SysWxMpDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.oss.dto.FileNameDto;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 * [description]：登录
 */
@Slf4j
@Component
public class WxMpKeywordHandler implements WxMpMessageHandler {
    @Autowired
    SysConfigService sysConfigService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        try {
            String tenantId = TenantContextHolder.getTenantId();
            tenantId ="1";
            Map<String, Object> map = sysConfigService.list(Wrappers.query(new SysConfig().setJvsTenantId(tenantId).setType(SysConfigTypeEnum.valueOf("WECHAT_MP_MESSAGE"))))
                    .stream()
                    .collect(Collectors.toMap(SysConfig::getName, SysConfig::getContent));
            SysWxMpDto sysWxMpSettings = BeanUtil.mapToBean(map, SysWxMpDto.class, false, CopyOptions.create());

            //获取对应的关键字回复内容
//            SysWxMpSettingsService bean = SpringContextUtil.getBean(SysWxMpSettingsService.class);
//            SysWxMpSettings sysWxMpSettings = bean.getOne(new LambdaQueryWrapper<>());
            log.info("获取的关键字信息为:{}", JSONUtil.toJsonStr(sysWxMpSettings));
            if (!sysWxMpSettings.getKeywordJson().isEmpty()) {
                List<WxKeywordData> data = sysWxMpSettings.getKeywordJson().parallelStream().map(e->e.toJavaObject(WxKeywordData.class)).collect(Collectors.toList());
                data=data.parallelStream().map(e-> BeanCopyUtil.copy(e,WxKeywordData.class)).collect(Collectors.toList());
                List<WxKeywordData> collect = data.stream().filter(e -> e.getKey().equals(wxMessage.getContent())).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    WxKeywordData wxKeywordData = collect.get(BigDecimal.ROUND_UP);
                    //获取外链地址
                    FileNameDto picUrl = wxKeywordData.getPicUrl();
                    WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
                    item.setDescription(wxKeywordData.getDescription());
                    item.setTitle(wxKeywordData.getTitle());
                    item.setPicUrl(picUrl.getFileLink());
                    item.setUrl(wxKeywordData.getUrl());
                    return WxMpXmlOutMessage.NEWS()
                            .addArticle(item)
                            .fromUser(wxMessage.getToUser())
                            .toUser(wxMessage.getFromUser())
                            .build();
                }
            }
            //如果用户的关键字 没有找到就再次把关键字信息发给用户
            return WxMpXmlOutMessage.TEXT()
                    .content(sysWxMpSettings.getKeywordText())
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        } catch (Exception e) {
            log.info("关键字消息错误", e);
        }
        return null;
    }


}
