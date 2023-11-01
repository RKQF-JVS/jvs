//package cn.bctools.message.push.handler.wechatwork.agent;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.WechatWorkAgentConfig;
//import cn.bctools.message.push.dto.messagePush.wechatwork.agent.MediaMessageDTO;
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
//import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
//import me.chanjar.weixin.cp.bean.message.WxCpMessage;
//import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
///**
// * 企业微信-图片消息
// *
// **/
//@Component
//@AllArgsConstructor
//public class AgentImageMessageHandler extends MessageHandler<MediaMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(MediaMessageDTO param) {
//        if(!param.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(param.getClientCode(),PlatformEnum.WECHAT_WORK_AGENT);
//        WechatWorkAgentConfig config = JSON.parseObject(clientDetail.getConfigValue(), WechatWorkAgentConfig.class);
//
//        WxCpServiceImpl wxCpService = SingletonUtil.get(config.getCorpId() + config.getSecret() + config.getAgentId(), () -> {
//            WxCpDefaultConfigImpl cpConfig = new WxCpDefaultConfigImpl();
//            cpConfig.setCorpId(config.getCorpId());
//            cpConfig.setCorpSecret(config.getSecret());
//            cpConfig.setAgentId(config.getAgentId());
//            WxCpServiceImpl wxCpService1 = new WxCpServiceImpl();
//            wxCpService1.setWxCpConfigStorage(cpConfig);
//            return wxCpService1;
//        });
//
//        //生成批次号
//        String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");
//
//        param.getDefinedReceivers().forEach(e ->{
//            MessagePushHis messagePushHis = new MessagePushHis()
//                    .setBatchNumber(batchNumber)
//                    .setMessageContent(JSON.toJSONString(param))
//                    .setUserId(e.getUserId())
//                    .setUserName(e.getUserName())
//                    .setClientCode(param.getClientCode())
//                    .setPlatform(PlatformEnum.WECHAT_WORK_AGENT)
//                    .setMessageType(MessageTypeEnum.WECHAT_WORK_AGENT_IMAGE);
//
//            WxCpMessage message = WxCpMessage.IMAGE()
//                    .agentId(config.getAgentId()) // 企业号应用ID
//                    .toUser(e.getReceiverConfig())
//                    .toParty(param.getToParty())
//                    .toTag(param.getToTag())
//                    .mediaId(param.getMediaId())
//                    .build();
//            try {
//                wxCpService.getMessageService().send(message);
//                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
//            } catch (Exception exception) {
//                messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
//                String eMessage = ExceptionUtil.getMessage(exception);
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
//        MediaMessageDTO dto = JSON.parseObject(his.getMessageContent(),MediaMessageDTO.class);
//        dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
