package cn.bctools.message.push.service;

import cn.bctools.message.push.dto.enums.MessageTypeEnum;

public interface MessageResendService {
    void resendMessage(String hisId);
    void resendMessage(String hisId, MessageTypeEnum messageTypeEnum);
}
