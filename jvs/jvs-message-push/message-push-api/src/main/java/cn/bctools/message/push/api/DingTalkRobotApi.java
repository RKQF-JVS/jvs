//package cn.bctools.message.push.api;
//
//import cn.bctools.common.utils.R;
//import cn.bctools.message.push.dto.messagePush.dingtalk.robot.*;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
///**
// * 钉钉群消息-卡片消息
// */
//@FeignClient(value = "message-push-mgr",contextId = "dingtalk-robot")
//public interface DingTalkRobotApi {
//
//    String prefix = "/ding/talk/robot";
//
//    /**
//     * 钉钉群消息-卡片消息-独立跳转类型
//     * @param dto 消息数据
//     * @return 执行状态
//     */
//    @PostMapping(prefix+"/multi")
//    R<Boolean> sendActionCardMultiMessage(@RequestBody ActionCardMultiMessageDTO dto);
//
//    /**
//     * 钉钉群消息-卡片消息-整体跳转类型
//     * @param dto 消息数据
//     * @return 执行状态
//     */
//    @PostMapping(prefix+"/single")
//    R<Boolean> sendActionCardSingleMessage(@RequestBody ActionCardSingleMessageDTO dto);
//
//    /**
//     * 钉钉群消息link类型
//     * @param dto 消息数据
//     * @return 执行状态
//     */
//    @PostMapping(prefix+"/link")
//    R<Boolean> sendLinkMessage(@RequestBody LinkMessageDTO dto);
//
//    /**
//     * 钉钉群消息markdown
//     * @param dto 消息数据
//     * @return 执行状态
//     */
//    @PostMapping(prefix+"/markdown")
//    R<Boolean> sendMarkdownMessage(@RequestBody MarkdownMessageDTO dto);
//
//    /**
//     * 钉钉群消息FeedCard
//     * @param dto 消息数据
//     * @return 执行状态
//     */
//    @PostMapping(prefix+"/feed_card")
//    R<Boolean> sendFeedCardMessage(@RequestBody FeedCardMessageDTO dto);
//
//    /**
//     * 钉钉群消息text
//     * @param dto 消息数据
//     * @return 执行状态
//     */
//    @PostMapping(prefix+"/text")
//    R<Boolean> sendTextMessage(@RequestBody TextMessageDTO dto);
//
//}
