package cn.bctools.message.push.feign;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.api.MessagePushUtilsApi;
import cn.bctools.message.push.dto.messagePush.MessageBatchSendDto;
import cn.bctools.message.push.service.MessagePushUtilsService;
import cn.bctools.message.push.utils.JvsUserComponent;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "[Feign] 消息发送工具")
public class MessagePushUtilsApiImpl implements MessagePushUtilsApi {

    private final MessagePushUtilsService messagePushUtilsService;
    private final JvsUserComponent jvsUserComponent;

    @Override
    public R batchSend(MessageBatchSendDto batchSendDto) {
        messagePushUtilsService.batchSend(batchSendDto,jvsUserComponent.getCurrentUser(batchSendDto.getClientCode()));
        return R.ok();
    }
}
