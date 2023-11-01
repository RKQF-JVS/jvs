package cn.bctools.auth.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.LoginLog;
import cn.bctools.auth.service.LoginLogService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Api(value = "登录日志", tags = "用户登录日志操作")
@RestController
@RequestMapping("/login/log")
public class LoginLogController {

    LoginLogService loginLogService;

    @Log(back = false)
    @GetMapping("/page")
    @ApiOperation(value = "分页查询", notes = "应用的分页查询")
    public R<Page<LoginLog>> page(Page<LoginLog> page, LoginLog apply) {
        loginLogService.page(page, Wrappers.lambdaQuery(apply).orderByDesc(LoginLog::getOperateTime));
        return R.ok(page);
    }

}
