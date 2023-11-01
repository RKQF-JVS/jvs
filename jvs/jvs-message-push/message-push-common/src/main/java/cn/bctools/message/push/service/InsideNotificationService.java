package cn.bctools.message.push.service;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;

public interface InsideNotificationService {

    /**
     * 站内
     * @param dto 数据
     */
    void send(InsideNotificationDto dto, UserDto pushUser);

    /**
     * 重新发送
     * @param pushHisId 发送历史
     */
    void resend(String pushHisId);
}
