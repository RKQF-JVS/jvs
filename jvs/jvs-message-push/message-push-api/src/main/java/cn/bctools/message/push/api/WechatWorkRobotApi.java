//package cn.bctools.message.push.api;
//
//import cn.bctools.common.utils.R;
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.*;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
///**
// * 企业微信 群机器
// */
//@FeignClient(value = "message-push-mgr",contextId = "webchat-work-robot")
//public interface WechatWorkRobotApi {
//    String prefix = "/webchat/work/robot";
//
//    /**
//     * 图片、文件消息
//     * @param messageDto 消息数据
//     * @return 执行信息
//     */
//    @PostMapping(prefix+"/image")
//    R<Boolean> sendWebChatImageMessage(@RequestBody ImageMessageDTO messageDto);
//
//    /**
//     * markdowm信息
//     * @param messageDto 消息数据
//     * @return 执行信息
//     */
//    @PostMapping(prefix+"/markdown")
//    R<Boolean> sendWebChatMarkDownMessage(@RequestBody MarkdownMessageDTO messageDto);
//
//    /**
//     * 图文消息
//     * @param messageDto 消息数据
//     * @return 执行信息
//     */
//    @PostMapping(prefix+"/news")
//    R<Boolean> sendWebChatNewsMessage(@RequestBody NewsMessageDTO messageDto);
//
//    /**
//     * 文本信息
//     * @param messageDto 消息数据
//     * @return 执行信息
//     */
//    @PostMapping(prefix+"/text")
//    R<Boolean> sendWebChatTextMessage(@RequestBody TextMessageDTO messageDto);
//
//}
