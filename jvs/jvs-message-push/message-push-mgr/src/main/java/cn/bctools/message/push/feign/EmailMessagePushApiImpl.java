package cn.bctools.message.push.feign;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.api.EmailMessagePushApi;
import cn.bctools.message.push.dto.messagePush.EmailMessageDto;
import cn.bctools.message.push.service.EmailMessagePushService;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "[Feign] 邮件接口")
public class EmailMessagePushApiImpl implements EmailMessagePushApi {

    private final EmailMessagePushService emailMessagePushService;
//    private final UserComponent userComponent;

    @Override
    public R<Boolean> sendEmail(EmailMessageDto messageDto) {
        emailMessagePushService.sendEmailMessage(messageDto, UserCurrentUtils.getCurrentUser());
        return R.ok(Boolean.TRUE);
    }

    @Override
    public R<Boolean> resendEmail(String pushHisId) {
        emailMessagePushService.resendEmailMessage(pushHisId);
        return R.ok(Boolean.TRUE);
    }
}
