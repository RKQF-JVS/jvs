//package cn.bctools.message.push.feign;
//
//import cn.bctools.common.utils.R;
//import cn.bctools.message.push.api.WechatWorkRobotApi;
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.ImageMessageDTO;
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.MarkdownMessageDTO;
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.NewsMessageDTO;
//import cn.bctools.message.push.dto.messagePush.wechatwork.robot.TextMessageDTO;
//import cn.bctools.message.push.service.WechatWorkRobotService;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequestMapping
//@RestController
//@AllArgsConstructor
//public class WechatWorkRobotApiImpl implements WechatWorkRobotApi {
//
//    private final WechatWorkRobotService wechatWorkRobotService;
//
//    @Override
//    public R<Boolean> sendWebChatImageMessage(ImageMessageDTO messageDto) {
//        wechatWorkRobotService.sendWebChatImageMessage(messageDto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendWebChatMarkDownMessage(MarkdownMessageDTO messageDto) {
//        wechatWorkRobotService.sendWebChatMarkDownMessage(messageDto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendWebChatNewsMessage(NewsMessageDTO messageDto) {
//        wechatWorkRobotService.sendWebChatNewsMessage(messageDto);
//        return R.ok();
//    }
//
//    @Override
//    public R<Boolean> sendWebChatTextMessage(TextMessageDTO messageDto) {
//        wechatWorkRobotService.sendWebChatTextMessage(messageDto);
//        return R.ok();
//    }
//}
