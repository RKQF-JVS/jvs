//package cn.bctools.message.push.controller;
//
//import io.swagger.annotations.Api;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * <p>
// * 消息配置--客户端 前端控制器
// * </p>
// *
// * @author wl
// * @since 2022-05-18
// */
//@RestController
//@RequestMapping("/message/config")
//@AllArgsConstructor
//@Api(tags ="消息配置--客户端")
//public class MessageConfigController {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigUtils messageConfigUtils;
//    private final ApplyServiceApi applyServiceApi;
//
//    @PostMapping
//    @ApiOperation("创建客户端")
//    @Deprecated
//    public R<MessageConfig> add(@RequestBody MessageConfig config){
//        messageConfigService.save(config);
//        return R.ok(config);
//    }
//
//    @PutMapping
//    @ApiOperation("修改客户端")
//    @Deprecated
//    public R<MessageConfig> update(@RequestBody MessageConfig config){
//        messageConfigService.updateById(config);
//        return R.ok(config);
//    }
//
//    @GetMapping
//    @ApiOperation("查询客户端")
//    public R<List<ApplyDto>> list(){
//        return R.ok(applyServiceApi.all().getData());
//    }
//
//    @DeleteMapping("/{id}")
//    @ApiOperation("删除客户端")
//    @Deprecated
//    public R<Boolean> del(@ApiParam("客户端id") @PathVariable("id")String id){
//        return R.ok(messageConfigService.removeById(id));
//    }
//
//    @DeleteMapping("/refresh")
//    @ApiOperation("刷新终端数据")
//    public R<Boolean> refresh(){
//        messageConfigUtils.synchronizeClientData();
//        return R.ok();
//    }
//}
