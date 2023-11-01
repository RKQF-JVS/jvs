package cn.bctools.message.push.service;

import cn.bctools.message.push.dto.messagePush.wechatwork.agent.*;

public interface WechatWorkAgentService {

    void sendWebChatWorkFileMessage(MediaMessageDTO messageDto);

    void sendWebChatImageMessage(MediaMessageDTO messageDto);

    void sendWebChatMarkDownMessage(MarkdownMessageDTO messageDto);

    void sendWebChatNewsMessage(NewsMessageDTO messageDto);

    void sendWebChatTextCardMessage(TextCardMessageDTO messageDto);

    void sendWebChatTextMessage(WeTextMessageDTO messageDto);

    void sendWebChatVideoMessage(VideoMessageDTO messageDto);
}
