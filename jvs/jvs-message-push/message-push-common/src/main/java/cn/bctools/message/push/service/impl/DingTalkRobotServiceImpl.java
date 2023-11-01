//package cn.bctools.message.push.service.impl;
//
//import cn.bctools.message.push.dto.messagePush.dingtalk.robot.*;
//import cn.bctools.message.push.handler.dingtalk.robot.*;
//import cn.bctools.message.push.service.DingTalkRobotService;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class DingTalkRobotServiceImpl implements DingTalkRobotService {
//
//    private final ActionCardMultiMessageHandler multiMessageHandler;
//    private final ActionCardSingleMessageHandler singleMessageHandler;
//    private final FeedCardMessageHandler feedCardMessageHandler;
//    private final RobotLinkMessageHandler robotLinkMessageHandler;
//    private final RobotMarkdownMessageHandler robotMarkdownMessageHandler;
//    private final RobotTextMessageHandler robotTextMessageHandler;
//
//
//    @Override
//    public void sendActionCardMultiMessage(ActionCardMultiMessageDTO dto) {
//        multiMessageHandler.handle(dto);
//    }
//
//    @Override
//    public void sendActionCardSingleMessage(ActionCardSingleMessageDTO dto) {
//        singleMessageHandler.handle(dto);
//    }
//
//    @Override
//    public void sendLinkMessage(LinkMessageDTO dto) {
//        robotLinkMessageHandler.handle(dto);
//    }
//
//    @Override
//    public void sendMarkdownMessage(MarkdownMessageDTO dto) {
//        robotMarkdownMessageHandler.handle(dto);
//
//    }
//
//    @Override
//    public void sendFeedCardMessage(FeedCardMessageDTO dto) {
//        feedCardMessageHandler.handle(dto);
//    }
//
//    @Override
//    public void sendTextMessage(TextMessageDTO dto) {
//        robotTextMessageHandler.handle(dto);
//    }
//}
