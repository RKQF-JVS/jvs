package cn.bctools.message.push.service.impl;

import cn.bctools.message.push.dto.messagePush.dingtalk.corp.*;
import cn.bctools.message.push.handler.dingtalk.corp.*;
import cn.bctools.message.push.service.DingTalkCorpService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DingTalkCorpServiceImpl implements DingTalkCorpService {

//    private final CorpActionCardMultiMessageHandler actionCardMultiMessageHandler;
//    private final CorpActionCardSingleMessageHandler actionCardSingleMessageHandler;
//    private final CorpLinkMessageHandler linkMessageHandler;
//    private final CorpMarkdownMessageHandler markdownMessageHandler;
//    private final CorpOaMessageHandler oaMessageHandler;
    private final CorpTextMessageHandler corpTextMessageHandler;


    @Override
    public void sendActionCardMultiMessage(ActionCardMultiMessageDTO dto) {
//        actionCardMultiMessageHandler.handle(dto);
    }

    @Override
    public void sendActionCardSingleMessage(ActionCardSingleMessageDTO dto) {
//        actionCardSingleMessageHandler.handle(dto);
    }

    @Override
    public void sendLinkMessage(LinkMessageDTO dto) {
//        linkMessageHandler.handle(dto);
    }

    @Override
    public void sendMarkdownMessage(MarkdownMessageDTO dto) {
//        markdownMessageHandler.handle(dto);
    }

    @Override
    public void sendOaMessage(OaMessageDTO dto) {
//        oaMessageHandler.handle(dto);
    }

    @Override
    public void sendTextMessage(TextMessageDTO dto) {
        corpTextMessageHandler.handle(dto);
    }
}
