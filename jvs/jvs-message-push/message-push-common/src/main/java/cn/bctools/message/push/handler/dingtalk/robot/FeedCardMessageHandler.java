//package cn.bctools.message.push.handler.dingtalk.robot;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.DingTalkRobotConfig;
//import cn.bctools.message.push.dto.messagePush.ReceiversDto;
//import cn.bctools.message.push.dto.messagePush.dingtalk.robot.FeedCardMessageDTO;
//import cn.bctools.message.push.entity.MessageConfigDetail;
//import cn.bctools.message.push.entity.MessagePushHis;
//import cn.bctools.message.push.dto.enums.*;
//import cn.bctools.message.push.handler.MessageHandler;
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import cn.bctools.message.push.service.MessageConfigService;
//import cn.bctools.message.push.service.MessagePushHisService;
//import cn.bctools.message.push.utils.MessagePushUtils;
//import cn.hutool.core.exceptions.ExceptionUtil;
//import com.alibaba.fastjson.JSON;
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.OapiRobotSendRequest;
//import com.dingtalk.api.response.OapiRobotSendResponse;
//import com.taobao.api.ApiException;
//import lombok.AllArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
///**
// * 钉钉群机器人ACTION_CARD_SINGLE类型消息处理器
// *
// **/
//@Component
//@AllArgsConstructor
//public class FeedCardMessageHandler extends MessageHandler<FeedCardMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(FeedCardMessageDTO param) {
//        if(!param.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(param.getClientCode(),PlatformEnum.DING_TALK_ROBOT);
//        if(clientDetail==null){
//            throw new BusinessException("当前客户端未配置详细");
//        }
//        DingTalkRobotConfig config = JSON.parseObject(clientDetail.getConfigValue(), DingTalkRobotConfig.class);
//
//        //获取接收人
//        List<String> receiverUsers = param.getDefinedReceivers().stream().map(ReceiversDto::getReceiverConfig).collect(Collectors.toList());
//
//        //生成批次号
//        String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");
//
//        MessagePushHis messagePushHis = new MessagePushHis()
//                .setBatchNumber(batchNumber)
//                .setMessageContent(JSON.toJSONString(param))
//                .setPlatform(PlatformEnum.DING_TALK_ROBOT)
//                .setMessageType(MessageTypeEnum.DING_TALK_ROBOT_FEED_CARD);
//
//        try {
//            DingTalkClient dingTalkClient = new DefaultDingTalkClient(config.getWebhook());
//            OapiRobotSendRequest request = new OapiRobotSendRequest();
//            request.setMsgtype("feedCard");
//            OapiRobotSendRequest.Feedcard feedcard = new OapiRobotSendRequest.Feedcard();
//            List<FeedCardMessageDTO.Item> items = param.getItems();
//            List<OapiRobotSendRequest.Links> links = new ArrayList<>();
//            for (FeedCardMessageDTO.Item item : items) {
//                OapiRobotSendRequest.Links link = new OapiRobotSendRequest.Links();
//                link.setTitle(item.getTitle());
//                link.setMessageURL(item.getMessageURL());
//                link.setPicURL(item.getPicURL());
//                links.add(link);
//            }
//            feedcard.setLinks(links);
//            request.setFeedCard(feedcard);
//            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//            at.setIsAtAll(param.isAtAll());
//            at.setAtMobiles(MessagePushUtils.getMobile(receiverUsers));
//            at.setAtUserIds(MessagePushUtils.getNotMobile(receiverUsers));
//            request.setAt(at);
//            OapiRobotSendResponse rsp = dingTalkClient.execute(request);
//            if (!rsp.isSuccess()) {
//                throw new IllegalStateException(rsp.getBody());
//            }
//            messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
//            messagePushHis.setErrorMsg(rsp.getBody());
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
//        FeedCardMessageDTO dto = JSON.parseObject(his.getMessageContent(),FeedCardMessageDTO.class);
//        //dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
