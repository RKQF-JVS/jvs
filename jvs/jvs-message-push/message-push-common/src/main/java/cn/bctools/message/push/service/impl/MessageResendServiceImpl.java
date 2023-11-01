package cn.bctools.message.push.service.impl;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.handler.AliSmsHandler;
import cn.bctools.message.push.handler.EmailMessageHandler;
import cn.bctools.message.push.handler.InsideNotificationHandler;
import cn.bctools.message.push.handler.wechatofficialaccount.TemplateMessageHandler;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.bctools.message.push.service.MessageResendService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageResendServiceImpl implements MessageResendService {

    private final MessagePushHisService messagePushHisService;
    private final AliSmsHandler aliSmsHandler;
    private final EmailMessageHandler emailMessageHandler;
    private final TemplateMessageHandler templateMessageHandler;
    private final InsideNotificationHandler insideNotificationHandler;

    @Override
    public void resendMessage(String hisId) {
        MessagePushHis byId = messagePushHisService.getById(hisId);
        this.resendMessage(hisId,byId.getMessageType());
    }

    @Override
    public void resendMessage(String hisId, MessageTypeEnum messageTypeEnum) {
        try {
            if(MessageTypeEnum.ALI_SMS.equals(messageTypeEnum)){
                aliSmsHandler.resend(hisId);
            }
            if(MessageTypeEnum.WECHAT_OFFICIAL_ACCOUNT_TEMPLATE.equals(messageTypeEnum)){
                templateMessageHandler.resend(hisId);
            }
            if(MessageTypeEnum.EMAIL.equals(messageTypeEnum)){
                insideNotificationHandler.resend(hisId);
            }
            if(MessageTypeEnum.INSIDE_NOTIFICATION.equals(messageTypeEnum)){
                emailMessageHandler.resend(hisId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("重发失败");
        }
    }
}
