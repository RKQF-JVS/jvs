package cn.bctools.message.push.feign;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.api.WechatWorkAgentApi;
import cn.bctools.message.push.dto.messagePush.wechatwork.agent.*;
import cn.bctools.message.push.service.WechatWorkAgentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
@AllArgsConstructor
public class WechatWorkAgentApiImpl implements WechatWorkAgentApi {

    private final WechatWorkAgentService wechatWorkAgentService;

    @Override
    public R<Boolean> sendWebChatWorkFileMessage(MediaMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatWorkFileMessage(messageDto);
        return R.ok();
    }

    @Override
    public R<Boolean> sendWebChatImageMessage(MediaMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatImageMessage(messageDto);
        return R.ok();
    }

    @Override
    public R<Boolean> sendWebChatMarkDownMessage(MarkdownMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatMarkDownMessage(messageDto);
        return R.ok();
    }

    @Override
    public R<Boolean> sendWebChatNewsMessage(NewsMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatNewsMessage(messageDto);
        return R.ok();
    }

    @Override
    public R<Boolean> sendWebChatTextCardMessage(TextCardMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatTextCardMessage(messageDto);
        return R.ok();
    }

    @Override
    public R<Boolean> sendWebChatTextMessage(WeTextMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatTextMessage(messageDto);
        return R.ok();
    }

    @Override
    public R<Boolean> sendWebChatVideoMessage(VideoMessageDTO messageDto) {
        wechatWorkAgentService.sendWebChatVideoMessage(messageDto);
        return R.ok();
    }
}
