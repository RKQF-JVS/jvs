package cn.bctools.message.push.service.impl;

import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.messagePush.EmailMessageDto;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.handler.AliSmsHandler;
import cn.bctools.message.push.handler.EmailMessageHandler;
import cn.bctools.message.push.handler.InsideNotificationHandler;
import cn.bctools.message.push.handler.wechatofficialaccount.TemplateMessageHandler;
import cn.bctools.message.push.service.MessagePushService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessagePushServiceImpl implements MessagePushService {

    private final EmailMessageHandler emailMessageHandler;
    private final AliSmsHandler aliSmsHandler;
    private final TemplateMessageHandler templateMessageHandler;
    private final InsideNotificationHandler insideNotificationHandler;

    @Override
    public void sendEmailMessage(EmailMessageDto messageDto) {
        emailMessageHandler.handle(messageDto);
    }

    @Override
    public void sendAliSms(AliSmsDto dto) throws Exception {
        aliSmsHandler.handle(dto);
    }

    @Override
    public void sendWechatTemplate(TemplateMessageDTO dto) {
        templateMessageHandler.handle(dto);
    }

    @Override
    public void sendInside(InsideNotificationDto dto) {
        insideNotificationHandler.handle(dto);
    }
}
