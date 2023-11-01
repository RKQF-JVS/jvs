package cn.bctools.message.push.service;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.message.push.dto.messagePush.EmailMessageDto;

public interface EmailMessagePushService {

    void sendEmailMessage(EmailMessageDto messageDto, UserDto pushUser);

    void resendEmailMessage(String pushHisId);

}
