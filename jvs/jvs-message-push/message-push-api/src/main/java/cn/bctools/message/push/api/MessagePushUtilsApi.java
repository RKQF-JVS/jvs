package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.MessageBatchSendDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "message-push-mgr", contextId = "message-push-utils")
public interface MessagePushUtilsApi {
    String prefix = "/message/utils/api";

    /**
     * 批量发送消息 需要自行设置接收人信息
     * @param batchSendDto
     * @return
     */
    @PostMapping(prefix+"/batch/send")
    R batchSend(@RequestBody MessageBatchSendDto batchSendDto);
}
