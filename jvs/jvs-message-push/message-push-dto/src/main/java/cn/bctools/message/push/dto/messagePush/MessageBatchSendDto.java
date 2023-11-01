package cn.bctools.message.push.dto.messagePush;

import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MessageBatchSendDto {

    /**
     * 阿里云短信消息
     */
    private List<AliSmsDto> aliSmsDtoList;

    /**
     * 邮件消息
     */
    private List<EmailMessageDto> emailDtoList;

    /**
     *站内信
     */
    private List<InsideNotificationDto> insideNotificationDtoList;

    /**
     * 微信模板消息
     */
    private List<TemplateMessageDTO> wxTemplateDtoList;

    private String clientCode;

    /**
     * 当前有token并且需要补充用户信息则设置为true
     */
    private Boolean complementIs = Boolean.FALSE;
}
