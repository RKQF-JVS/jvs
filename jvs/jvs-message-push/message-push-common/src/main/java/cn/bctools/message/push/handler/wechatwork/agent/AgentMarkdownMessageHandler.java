//package cn.bctools.message.push.handler.wechatwork.agent;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.WechatWorkAgentConfig;
//import cn.bctools.message.push.dto.messagePush.wechatwork.agent.MarkdownMessageDTO;
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
// * 企业微信Markdown消息handler
// *
// **/
//@Component
//@AllArgsConstructor
//public class AgentMarkdownMessageHandler extends MessageHandler<MarkdownMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(MarkdownMessageDTO dto) {
//        if(!dto.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(dto.getClientCode(),PlatformEnum.WECHAT_WORK_AGENT);
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
//        dto.getDefinedReceivers().forEach(e ->{
//            MessagePushHis messagePushHis = new MessagePushHis()
//                    .setMessageContent(JSON.toJSONString(dto))
//                    .setBatchNumber(batchNumber)
//                    .setPlatform(PlatformEnum.WECHAT_WORK_AGENT)
//                    .setUserId(e.getUserId())
//                    .setUserName(e.getUserName())
//                    .setClientCode(dto.getClientCode())
//                    .setMessageType(MessageTypeEnum.WECHAT_WORK_AGENT_MARKDOWN);
//            WxCpMessage message = WxCpMessage.MARKDOWN()
//                    .agentId(config.getAgentId()) // 企业号应用ID
//                    .toUser(e.getReceiverConfig())
//                    .toParty(dto.getToParty())
//                    .toTag(dto.getToTag())
//                    .content(dto.getContent())
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
//        MarkdownMessageDTO dto = JSON.parseObject(his.getMessageContent(),MarkdownMessageDTO.class);
//        dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
