//package cn.bctools.message.push.controller;
//
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import io.swagger.annotations.Api;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * <p>
// * 配置详情 前端控制器
// * </p>
// *
// * @author wl
// * @since 2022-05-18
// */
//@RestController
//@RequestMapping("/message/config/detail")
//@Api(tags = "消息配置--客户端--配置详情")
//@AllArgsConstructor
//public class MessageConfigDetailController {
//
//    private final MessageConfigDetailService messageConfigDetailService;
//
//    @PostMapping
//    @ApiOperation("创建客户端详细配置")
//    public R<MessageConfigDetail> add(@RequestBody MessageConfigDetail dto){
//        if(PlatformEnum.EMAIL.equals(dto.getPlatform())){
//            EmailConfig emailConfig = JSON.parseObject(dto.getConfigValue(), EmailConfig.class);
//            boolean hasNull = BaseConfig.hasNull(emailConfig);
//            if(hasNull){
//                throw new BusinessException("邮件配置不完善");
//            }
//        }
//        if(PlatformEnum.DING_TALK_CORP.equals(dto.getPlatform())){
//            DingTalkCorpConfig dingTalkCorpConfig = JSON.parseObject(dto.getConfigValue(), DingTalkCorpConfig.class);
//            boolean hasNull = BaseConfig.hasNull(dingTalkCorpConfig);
//            if(hasNull){
//                throw new BusinessException("钉钉-工作通知配置不完善");
//            }
//        }
//        if(PlatformEnum.DING_TALK_ROBOT.equals(dto.getPlatform())){
//            DingTalkRobotConfig dingTalkRobotConfig = JSON.parseObject(dto.getConfigValue(), DingTalkRobotConfig.class);
//            boolean hasNull = BaseConfig.hasNull(dingTalkRobotConfig);
//            if(hasNull){
//                throw new BusinessException("钉钉-群机器人配置不完善");
//            }
//        }
//        if(PlatformEnum.WECHAT_OFFICIAL_ACCOUNT.equals(dto.getPlatform())){
//            WechatOfficialAccountConfig config = JSON.parseObject(dto.getConfigValue(), WechatOfficialAccountConfig.class);
//            boolean hasNull = BaseConfig.hasNull(config);
//            if(hasNull){
//                throw new BusinessException("微信公众号配置不完善");
//            }
//        }
//        if(PlatformEnum.WECHAT_WORK_AGENT.equals(dto.getPlatform())){
//            WechatWorkAgentConfig wechatWorkAgentConfig = JSON.parseObject(dto.getConfigValue(), WechatWorkAgentConfig.class);
//            boolean hasNull = BaseConfig.hasNull(wechatWorkAgentConfig);
//            if(hasNull){
//                throw new BusinessException("企业微信-应用消息配置不完善");
//            }
//        }
//        if(PlatformEnum.WECHAT_WORK_ROBOT.equals(dto.getPlatform())){
//            WechatWorkRobotConfig wechatWorkRobotConfig = JSON.parseObject(dto.getConfigValue(), WechatWorkRobotConfig.class);
//            boolean hasNull = BaseConfig.hasNull(wechatWorkRobotConfig);
//            if(hasNull){
//                throw new BusinessException("企业微信-群机器人配置不完善");
//            }
//        }
//        if(PlatformEnum.ALI_SMS.equals(dto.getPlatform())){
//            AliSmsConfig aliSmsConfig = JSON.parseObject(dto.getConfigValue(), AliSmsConfig.class);
//            boolean hasNull = BaseConfig.hasNull(aliSmsConfig);
//            if(hasNull){
//                throw new BusinessException("短息配置不完善");
//            }
//        }
//        messageConfigDetailService.save(dto);
//        return R.ok(dto);
//    }
//
//    @PutMapping
//    @ApiOperation("修改客户端详细配置")
//    public R<MessageConfigDetail> update(@RequestBody MessageConfigDetail config){
//        messageConfigDetailService.updateById(config);
//        return R.ok(config);
//    }
//
//    @GetMapping("/{id}")
//    @ApiOperation("获取客户端详细配置")
//    public R<MessageConfigDetail> query(@ApiParam("客户端id")@PathVariable("id")String id,MessageConfigDetail dto){
//        MessageConfigDetail messageConfigDetail = messageConfigDetailService.getOne(new LambdaQueryWrapper<MessageConfigDetail>().eq(MessageConfigDetail::getConfigId, id).eq(MessageConfigDetail::getPlatform, dto.getPlatform()));
//        if(messageConfigDetail==null){
//            throw new BusinessException("未找到配置");
//        }
//        if(messageConfigDetail.getConfigValue()!=null){
//            messageConfigDetail.setConfigDetail(JSON.parseObject(messageConfigDetail.getConfigValue(),messageConfigDetail.getPlatform().getConfigType()));
//        }
//        return R.ok(messageConfigDetail);
//    }
//
//    @DeleteMapping("/{id}")
//    @ApiOperation("删除客户端详细配置")
//    public R<Boolean> del(@ApiParam("客户端详细配置id") @PathVariable("id")String id){
//        return R.ok(messageConfigDetailService.removeById(id));
//    }
//}
