package cn.bctools.message.push.service;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.message.push.dto.messagePush.MessageBatchSendDto;

public interface MessagePushUtilsService {

    void batchSend(MessageBatchSendDto batchSendDto, UserDto pushUser);
}
