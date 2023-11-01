//package cn.bctools.message.push.handler.wechatofficialaccount;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.WechatOfficialAccountConfig;
//import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.NewsMessageDTO;
//import cn.bctools.message.push.entity.MessageConfigDetail;
//import cn.bctools.message.push.entity.MessagePushHis;
//import cn.bctools.message.push.dto.enums.*;
//import cn.bctools.message.push.handler.MessageHandler;
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import cn.bctools.message.push.service.MessageConfigService;
//import cn.bctools.message.push.service.MessagePushHisService;
//import cn.bctools.message.push.utils.SingletonUtil;
//import cn.hutool.core.exceptions.ExceptionUtil;
//import com.alibaba.fastjson.JSON;
//import lombok.AllArgsConstructor;
//import me.chanjar.weixin.common.error.WxErrorException;
//import me.chanjar.weixin.mp.api.WxMpService;
//import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
//import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
//import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
///**
// * 微信公众模板消息handler
// *
// **/
//@Component
//@AllArgsConstructor
//public class MpNewsMessageHandler extends MessageHandler<NewsMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(NewsMessageDTO param) {
//        if(!param.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(param.getClientCode(),PlatformEnum.WECHAT_OFFICIAL_ACCOUNT);
//        WechatOfficialAccountConfig config = JSON.parseObject(clientDetail.getConfigValue(), WechatOfficialAccountConfig.class);
//
//        WxMpService wxService = SingletonUtil.get(config.getAppId() + config.getSecret(), () -> {
//            WxMpDefaultConfigImpl mpConfig = new WxMpDefaultConfigImpl();
//            mpConfig.setAppId(config.getAppId());
//            mpConfig.setSecret(config.getSecret());
//            WxMpService wxService1 = new WxMpServiceImpl();
//            wxService1.setWxMpConfigStorage(mpConfig);
//            return wxService1;
//        });
//
//        param.getDefinedReceivers().forEach(receiverUser -> {
//            WxMpKefuMessage.WxArticle article = new WxMpKefuMessage.WxArticle();
//            article.setUrl(param.getUrl());
//            article.setPicUrl(param.getPicUrl());
//            article.setDescription(param.getDescription());
//            article.setTitle(param.getTitle());
//
//            WxMpKefuMessage message = WxMpKefuMessage.NEWS()
//                    .toUser(receiverUser.getReceiverConfig())
//                    .addArticle(article)
//                    .build();
//
//            //生成批次号
//            String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");
//
//            MessagePushHis messagePushHis = new MessagePushHis()
//                    .setBatchNumber(batchNumber)
//                    .setMessageContent(JSON.toJSONString(param))
//                    .setPlatform(PlatformEnum.WECHAT_OFFICIAL_ACCOUNT)
//                    .setUserId(receiverUser.getUserId())
//                    .setUserName(receiverUser.getUserName())
//                    .setClientCode(param.getClientCode())
//                    .setMessageType(MessageTypeEnum.WECHAT_OFFICIAL_ACCOUNT_NEWS);
//            try {
//                wxService.getKefuService().sendKefuMessage(message);
//                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
//            } catch (WxErrorException e) {
//                e.printStackTrace();
//                messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
//                String eMessage = ExceptionUtil.getMessage(e);
//                eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
//                messagePushHis.setErrorMsg(eMessage);
//            }
//            messagePushHisService.save(messagePushHis);
//        });
//    }
//
//    @Override
//    public void resend(String pushHisId) throws Exception {
//        MessagePushHis his = messagePushHisService.getById(pushHisId);
//        NewsMessageDTO dto = JSON.parseObject(his.getMessageContent(),NewsMessageDTO.class);
//        dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
