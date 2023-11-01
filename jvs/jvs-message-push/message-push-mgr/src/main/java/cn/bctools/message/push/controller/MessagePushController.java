//package cn.bctools.message.push.controller;
//
//import io.swagger.annotations.Api;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/message/push")
//@AllArgsConstructor
//@Api(tags = "消息发送")
//public class MessagePushController {
//
//    private final MessagePushService messagePushService;
//
//    @PostMapping("/email")
//    @ApiOperation("发送邮件")
//    public R<Boolean> email(@RequestBody EmailMessageDto messageDto){
//        messagePushService.sendEmailMessage(messageDto);
//        return R.ok();
//    }
//
//    @PostMapping("/aliSms")
//    @ApiOperation("发送短信")
//    public R<Boolean> aliSms(@RequestBody AliSmsDto dto) throws Exception {
//        messagePushService.sendAliSms(dto);
//        return R.ok();
//    }
//
//    @PostMapping("/wechat_official_template")
//    @ApiOperation("微信公众号 模板消息")
//    public R<Boolean> template(@RequestBody TemplateMessageDTO dto) throws Exception {
//        messagePushService.sendWechatTemplate(dto);
//        return R.ok();
//    }
//
//    @PostMapping("/inside")
//    @ApiOperation("站内")
//    public R<Boolean> inside(@RequestBody InsideNotificationDto dto) throws Exception {
//        messagePushService.sendInside(dto);
//        return R.ok();
//    }
//}
