//package cn.bctools.message.push.handler.dingtalk.corp;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.DingTalkCorpConfig;
//import cn.bctools.message.push.dto.messagePush.ReceiversDto;
//import cn.bctools.message.push.dto.messagePush.dingtalk.corp.LinkMessageDTO;
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
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
//import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
//import com.taobao.api.ApiException;
//import lombok.AllArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
///**
// * 钉钉工作通知文本类型消息处理器
// *
// **/
//@Component
//@AllArgsConstructor
//public class CorpLinkMessageHandler extends MessageHandler<LinkMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(LinkMessageDTO param) {
//        if(!param.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(param.getClientCode(),PlatformEnum.DING_TALK_CORP);
//        if(clientDetail==null){
//            throw new BusinessException("当前客户端未配置详细");
//        }
//        DingTalkCorpConfig config = JSON.parseObject(clientDetail.getConfigValue(), DingTalkCorpConfig.class);
//
//        //获取接收人
//        List<String> receiverUsers = param.getDefinedReceivers().stream().map(ReceiversDto::getReceiverConfig).collect(Collectors.toList());
//
//        DingTalkClient dingTalkClient = SingletonUtil.get("dinging-" + config.getAppKey() + config.getAppSecret(),
//                (SingletonUtil.Factory<DingTalkClient>) () -> new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2"));
//        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
//        request.setAgentId(Long.valueOf(config.getAgentId()));
//        request.setUseridList(String.join(",", receiverUsers));
//        request.setDeptIdList(param.getDeptIdList());
//        request.setToAllUser(param.isToAllUser());
//
//        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
//        msg.setMsgtype("link");
//        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
//        msg.getLink().setTitle(param.getTitle());
//        msg.getLink().setText(param.getText());
//        msg.getLink().setMessageUrl(param.getMessageUrl());
//        msg.getLink().setPicUrl(param.getPicUrl());
//        request.setMsg(msg);
//
//        //生成批次号
//        String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");
//
//        MessagePushHis messagePushHis = new MessagePushHis()
//                .setBatchNumber(batchNumber)
//                .setMessageContent(JSON.toJSONString(param))
//                .setPlatform(PlatformEnum.DING_TALK_CORP)
//                .setMessageType(MessageTypeEnum.DING_TALK_COPR_LINK);
//
//        try {
//            OapiMessageCorpconversationAsyncsendV2Response rsp = dingTalkClient.execute(request, AccessTokenUtils.getAccessToken(config.getAppKey(), config.getAppSecret()));
//            if (!rsp.isSuccess()) {
//                throw new IllegalStateException(rsp.getBody());
//            }
//            messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
//        } catch (ApiException e) {
//            e.printStackTrace();
//            messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
//            String eMessage = ExceptionUtil.getMessage(e);
//            eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
//            messagePushHis.setErrorMsg(eMessage);
//        }
//        messagePushHisService.save(messagePushHis);
//    }
//
//    @Override
//    public void resend(String pushHisId) throws Exception {
//        MessagePushHis his = messagePushHisService.getById(pushHisId);
//        LinkMessageDTO dto = JSON.parseObject(his.getMessageContent(),LinkMessageDTO.class);
//        dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
