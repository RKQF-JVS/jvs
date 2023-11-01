package cn.bctools.message.push.service.impl;

import cn.bctools.message.push.dto.messagePush.wechatwork.agent.*;
import cn.bctools.message.push.handler.wechatwork.agent.AgentTextMessageHandler;
import cn.bctools.message.push.service.WechatWorkAgentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WechatWorkAgentServiceImpl implements WechatWorkAgentService {
//
//    private final AgentFileMessageHandler agentFileMessageHandler;
//    private final AgentImageMessageHandler agentImageMessageHandler;
//    private final AgentMarkdownMessageHandler agentMarkdownMessageHandler;
//    private final AgentNewsMessageHandler agentNewsMessageHandler;
//    private final AgentTextCardMessageHandler agentTextCardMessageHandler;
    private final AgentTextMessageHandler agentTextMessageHandler;
//    private final AgentVideoMessageHandler agentVideoMessageHandler;

    @Override
    public void sendWebChatWorkFileMessage(MediaMessageDTO messageDto) {
//        agentFileMessageHandler.handle(messageDto);
    }

    @Override
    public void sendWebChatImageMessage(MediaMessageDTO messageDto) {
//        agentImageMessageHandler.handle(messageDto);
    }

    @Override
    public void sendWebChatMarkDownMessage(MarkdownMessageDTO messageDto) {
//        agentMarkdownMessageHandler.handle(messageDto);
    }

    @Override
    public void sendWebChatNewsMessage(NewsMessageDTO messageDto) {
//        agentNewsMessageHandler.handle(messageDto);
    }

    @Override
    public void sendWebChatTextCardMessage(TextCardMessageDTO messageDto) {
//        agentTextCardMessageHandler.handle(messageDto);
    }

    @Override
    public void sendWebChatTextMessage(WeTextMessageDTO messageDto) {
        agentTextMessageHandler.handle(messageDto);
    }

    @Override
    public void sendWebChatVideoMessage(VideoMessageDTO messageDto) {
//        agentVideoMessageHandler.handle(messageDto);
    }
}
