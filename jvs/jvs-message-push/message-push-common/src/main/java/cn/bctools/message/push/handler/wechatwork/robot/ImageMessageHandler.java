//package cn.bctools.message.push.handler.wechatwork.robot;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.WechatWorkRobotConfig;
//import cn.bctools.message.push.dto.enums.*;
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.ImageMessageDTO;
//import cn.bctools.message.push.entity.MessageConfigDetail;
//import cn.bctools.message.push.entity.MessagePushHis;
//import cn.bctools.message.push.handler.MessageHandler;
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import cn.bctools.message.push.service.MessageConfigService;
//import cn.bctools.message.push.service.MessagePushHisService;
//import cn.hutool.core.exceptions.ExceptionUtil;
//import cn.hutool.core.util.ReUtil;
//import cn.hutool.http.HttpUtil;
//import cn.hutool.json.JSONObject;
//import com.alibaba.fastjson.JSON;
//import lombok.AllArgsConstructor;
//import me.chanjar.weixin.common.error.WxErrorException;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * 企业微信-图片消息
// *
// **/
//@Component
//@AllArgsConstructor
//public class ImageMessageHandler extends MessageHandler<ImageMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(ImageMessageDTO param) {
//        if(!param.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(param.getClientCode(),PlatformEnum.WECHAT_WORK_ROBOT);
//        WechatWorkRobotConfig config = JSON.parseObject(clientDetail.getConfigValue(), WechatWorkRobotConfig.class);
//
//        //生成批次号
//        String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");
//
//        MessagePushHis messagePushHis = new MessagePushHis()
//                .setBatchNumber(batchNumber)
//                .setMessageContent(JSON.toJSONString(param))
//                .setPlatform(PlatformEnum.WECHAT_WORK_ROBOT)
//                .setMessageType(MessageTypeEnum.WECHAT_WORK_ROBOT_IMAGE);
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("base64", param.getBase64());
//            jsonObject.put("md5", param.getMd5());
//            List<String> mentionedList = new ArrayList<>();
//            List<String> mentionedMobileList = new ArrayList<>();
//            param.getDefinedReceivers().forEach(receiverUser -> {
//                String receiverConfig = receiverUser.getReceiverConfig();
//                receiverConfig = receiverConfig.equals("all") ? "@all" : receiverConfig; // 补一下at全部人的@符号
//                boolean isMobile = ReUtil.isMatch("^[1][3,4,5,6,7,8,9][0-9]{9}$", receiverConfig);
//                if (isMobile) {
//                    mentionedMobileList.add(receiverConfig);
//                } else {
//                    mentionedList.add(receiverConfig);
//                }
//            });
//
//            jsonObject.put("mentioned_list", mentionedList);
//            jsonObject.put("mentioned_mobile_list", mentionedMobileList);
//            JSONObject messageParam = new JSONObject();
//            messageParam.put("msgtype", "image");
//            messageParam.put("image", jsonObject);
//            String post = HttpUtil.post(config.getWebhook(), messageParam.toString());
//            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") JSONObject result = new JSONObject(post);
//            if (result.getInt("errcode") != 0) {
//                throw new WxErrorException(post);
//            }
//            messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
//        } catch (WxErrorException e) {
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
//        ImageMessageDTO dto = JSON.parseObject(his.getMessageContent(),ImageMessageDTO.class);
//        //dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
