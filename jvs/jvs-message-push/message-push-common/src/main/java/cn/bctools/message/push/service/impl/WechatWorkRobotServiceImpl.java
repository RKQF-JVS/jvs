//package cn.bctools.message.push.service.impl;
//
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.*;
//import cn.bctools.message.push.handler.wechatwork.robot.*;
//import cn.bctools.message.push.service.WechatWorkRobotService;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class WechatWorkRobotServiceImpl implements WechatWorkRobotService {
//
//    private final ImageMessageHandler imageMessageHandler;
//    private final MarkdownMessageHandler markdownMessageHandler;
//    private final NewsMessageHandler newsMessageHandler;
//    private final TextMessageHandler textMessageHandler;
//
//    @Override
//    public void sendWebChatImageMessage(ImageMessageDTO messageDto) {
//        imageMessageHandler.handle(messageDto);
//    }
//
//    @Override
//    public void sendWebChatMarkDownMessage(MarkdownMessageDTO messageDto) {
//        markdownMessageHandler.handle(messageDto);
//    }
//
//    @Override
//    public void sendWebChatNewsMessage(NewsMessageDTO messageDto) {
//        newsMessageHandler.handle(messageDto);
//    }
//
//    @Override
//    public void sendWebChatTextMessage(TextMessageDTO messageDto) {
//        textMessageHandler.handle(messageDto);
//    }
//}
