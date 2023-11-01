package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.EmailMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 邮件
 */
@FeignClient(value = "message-push-mgr", contextId = "email")
public interface EmailMessagePushApi {

    /**
     * 邮件
     *
     * @param messageDto 消息数据
     * @return 执行状态
     */
    @PostMapping("/email")
    R<Boolean> sendEmail(@RequestBody EmailMessageDto messageDto);

    /**
     * 重新发送邮件
     *
     * @param pushHisId 消息数据
     * @return 执行状态
     */
    @GetMapping("/email/resend")
    R<Boolean> resendEmail(String pushHisId);

}
