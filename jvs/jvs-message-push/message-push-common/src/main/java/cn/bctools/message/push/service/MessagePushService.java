package cn.bctools.message.push.service;

import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.messagePush.EmailMessageDto;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;

public interface MessagePushService {

    void sendEmailMessage(EmailMessageDto messageDto);

    void sendAliSms(AliSmsDto dto) throws Exception;

    void sendWechatTemplate(TemplateMessageDTO dto);

    void sendInside(InsideNotificationDto dto);
}
