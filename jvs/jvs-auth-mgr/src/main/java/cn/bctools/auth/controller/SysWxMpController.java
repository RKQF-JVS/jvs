package cn.bctools.auth.controller;//package cn.bctools.auth.controller;
//
//import cn.bctools.auth.entity.SysWxMpSettings;
//import cn.bctools.auth.service.SysWxMpSettingsService;
//import cn.bctools.common.utils.R;
//import cn.bctools.log.annotation.Log;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//
///**
// * @author Administrator
// * @ClassName: DeptController
// * @Description: 部门管理(这里用一句话描述这个类的作用)
// */
//@Slf4j
//@AllArgsConstructor
//@Api(value = "微信公众号配置", tags = "微信公众号")
//@RestController
//@RequestMapping("/wx/settings")
//public class SysWxMpController {
//    private final SysWxMpSettingsService sysWxMpSettingsService;
//
//
//    @Log
//    @PostMapping
//    @ApiOperation(value = "修改配置信息")
//    public R<SysWxMpSettings> add(@RequestBody SysWxMpSettings sysWxMpSettings) {
//        sysWxMpSettingsService.updateById(sysWxMpSettings);
//        return R.ok(sysWxMpSettings);
//    }
//
//    @Log
//    @GetMapping
//    @ApiOperation(value = "获取数据")
//    public R<SysWxMpSettings> getData() {
//        SysWxMpSettings wxMpSettings = sysWxMpSettingsService.getOne(new LambdaQueryWrapper<>());
//        if (ObjectUtil.isNull(wxMpSettings)) {
//            wxMpSettings = new SysWxMpSettings().setWelcomeText("欢迎关注!").setKeywordText("欢迎关注!").setKeywordJson(new ArrayList<>()).setSubscribeUrl(new ArrayList<>());
//            sysWxMpSettingsService.save(wxMpSettings);
//        }
//        return R.ok(wxMpSettings);
//    }
//
//}
