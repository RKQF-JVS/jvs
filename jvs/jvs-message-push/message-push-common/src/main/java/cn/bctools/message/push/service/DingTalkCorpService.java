package cn.bctools.message.push.service;

import cn.bctools.message.push.dto.messagePush.dingtalk.corp.*;

public interface DingTalkCorpService {

    void sendActionCardMultiMessage(ActionCardMultiMessageDTO dto);

    void sendActionCardSingleMessage(ActionCardSingleMessageDTO dto);

    void sendLinkMessage(LinkMessageDTO dto);

    void sendMarkdownMessage(MarkdownMessageDTO dto);

    void sendOaMessage(OaMessageDTO dto);

    void sendTextMessage(TextMessageDTO dto);
}
