package cn.bctools.message.push.service.impl;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.bctools.message.push.handler.InsideNotificationHandler;
import cn.bctools.message.push.service.InsideNotificationService;
import cn.bctools.message.push.utils.MessagePushHisUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class InsideNotificationServiceImpl implements InsideNotificationService {

    private final InsideNotificationHandler insideNotificationHandler;
    private final MessagePushHisUtils messagePushHisUtils;
    @Override
    public void send(InsideNotificationDto dto, UserDto pushUser) {
        try {
            //设置租户id
            TenantContextHolder.setTenantId(Optional.ofNullable(pushUser).map(UserDto::getTenantId).orElse("1"));
            String batchNumber = messagePushHisUtils.saveHis(dto,pushUser, PlatformEnum.INSIDE_NOTIFICATION, MessageTypeEnum.INSIDE_NOTIFICATION);
            insideNotificationHandler.handle(batchNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resend(String pushHisId) {
        try {
            insideNotificationHandler.resend(pushHisId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
