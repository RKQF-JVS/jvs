//package cn.bctools.message.push.feign;
//
//import cn.bctools.common.utils.R;
//import cn.bctools.message.push.api.DingTalkRobotApi;
//import cn.bctools.message.push.dto.messagePush.dingtalk.robot.*;
//import cn.bctools.message.push.service.DingTalkRobotService;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequestMapping
//@RestController
//@AllArgsConstructor
//public class DingTalkRobotApiImpl implements DingTalkRobotApi {
//
//    private final DingTalkRobotService dingTalkRobotService;
//
//    @Override
//    public R<Boolean> sendActionCardMultiMessage(ActionCardMultiMessageDTO dto) {
//        dingTalkRobotService.sendActionCardMultiMessage(dto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendActionCardSingleMessage(ActionCardSingleMessageDTO dto) {
//        dingTalkRobotService.sendActionCardSingleMessage(dto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendLinkMessage(LinkMessageDTO dto) {
//        dingTalkRobotService.sendLinkMessage(dto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendMarkdownMessage(MarkdownMessageDTO dto) {
//        dingTalkRobotService.sendMarkdownMessage(dto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendFeedCardMessage(FeedCardMessageDTO dto) {
//        dingTalkRobotService.sendFeedCardMessage(dto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendTextMessage(TextMessageDTO dto) {
//        dingTalkRobotService.sendTextMessage(dto);
//        return R.ok();
//    }
//}
