package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.dingtalk.corp.TextMessageDTO;
import cn.bctools.message.push.dto.messagePush.wechatwork.agent.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 企业微信
 */
@FeignClient(value = "message-push-mgr",contextId = "webchat-work-agent")
public interface WechatWorkAgentApi {
    String prefix = "/webchat/work/agent";

    /**
     * 文件消息
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/file")
    R<Boolean> sendWebChatWorkFileMessage(@RequestBody MediaMessageDTO messageDto);

    /**
     * 图片消息
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/image")
    R<Boolean> sendWebChatImageMessage(@RequestBody MediaMessageDTO messageDto);

    /**
     * 企业微信Markdown
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/markdown")
    R<Boolean> sendWebChatMarkDownMessage(@RequestBody MarkdownMessageDTO messageDto);

    /**
     * 企业微信图文消息
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/news")
    R<Boolean> sendWebChatNewsMessage(@RequestBody NewsMessageDTO messageDto);

    /**
     * 卡片信息
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/text/card")
    R<Boolean> sendWebChatTextCardMessage(@RequestBody TextCardMessageDTO messageDto);

    /**
     * 文本消息
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/text")
    R<Boolean> sendWebChatTextMessage(@RequestBody WeTextMessageDTO messageDto);

    /**
     * 视频消息类型
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix+"/video")
    R<Boolean> sendWebChatVideoMessage(@RequestBody VideoMessageDTO messageDto);
}
