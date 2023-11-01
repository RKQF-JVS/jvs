package cn.bctools.message.push.service.impl;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.EmailMessageDto;
import cn.bctools.message.push.handler.EmailMessageHandler;
import cn.bctools.message.push.service.EmailMessagePushService;
import cn.bctools.message.push.utils.MessagePushHisUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EmailMessagePushServiceImpl implements EmailMessagePushService {

    private final EmailMessageHandler emailMessageHandler;
    private final MessagePushHisUtils messagePushHisUtils;

    @Override
    public void sendEmailMessage(EmailMessageDto messageDto, UserDto pushUser) {
        try {
            //设置租户id
            TenantContextHolder.setTenantId(Optional.ofNullable(pushUser).map(UserDto::getTenantId).orElse("1"));
            String batchNumber = messagePushHisUtils.saveHis(messageDto,pushUser, PlatformEnum.EMAIL, MessageTypeEnum.EMAIL);
            emailMessageHandler.handle(batchNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resendEmailMessage(String pushHisId) {
        try {
            emailMessageHandler.resend(pushHisId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
