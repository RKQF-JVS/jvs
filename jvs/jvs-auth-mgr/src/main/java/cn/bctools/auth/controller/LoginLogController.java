package cn.bctools.auth.controller;

import cn.bctools.auth.entity.LoginLog;
import cn.bctools.auth.service.LoginLogService;
import cn.bctools.auth.vo.LoginLogPageReqVo;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public R<Page<LoginLog>> page(Page<LoginLog> page, LoginLogPageReqVo reqVo) {
        loginLogService.page(page, Wrappers.<LoginLog>lambdaQuery()
                .eq(StringUtils.isNotBlank(reqVo.getAccountName()), LoginLog::getAccountName, reqVo.getAccountName())
                .eq(StringUtils.isNotBlank(reqVo.getRealName()), LoginLog::getRealName, reqVo.getRealName())
                .eq(ObjectNull.isNotNull(reqVo.getStatus()), LoginLog::getStatus, reqVo.getStatus())
                .eq(StringUtils.isNotBlank(reqVo.getLoginType()), LoginLog::getLoginType, reqVo.getLoginType())
                .eq(StringUtils.isNotBlank(reqVo.getClientId()), LoginLog::getClientId, reqVo.getClientId())
                .between(ObjectUtil.isNotNull(reqVo.getBeginDate()) && ObjectUtil.isNotNull(reqVo.getEndDate())
                        , LoginLog::getOperateTime, reqVo.getBeginDate(), reqVo.getEndDate())
                .orderByDesc(LoginLog::getOperateTime));
        return R.ok(page);
    }


    @Log(back = false)
    @GetMapping("/loginType")
    @ApiOperation(value = "登录类型", notes = "获取登录类型")
    public R<List> loginType() {
        List<LoginLog> list = loginLogService.list(Wrappers.<LoginLog>lambdaQuery().select(LoginLog::getLoginType).groupBy(LoginLog::getLoginType));
        return R.ok(list);
    }


    @Log(back = false)
    @GetMapping("/platform/page")
    @ApiOperation(value = "平台分页查询", notes = "应用的分页查询")
    public R<Page<LoginLog>> platform(Page<LoginLog> page, LoginLogPageReqVo reqVo) {
        if (!UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            return R.failed("权限不足");
        }
        //清除数据
        TenantContextHolder.clear();
        loginLogService.page(page, Wrappers.<LoginLog>lambdaQuery()
                .eq(StringUtils.isNotBlank(reqVo.getAccountName()), LoginLog::getAccountName, reqVo.getAccountName())
                .eq(StringUtils.isNotBlank(reqVo.getRealName()), LoginLog::getRealName, reqVo.getRealName())
                .eq(StringUtils.isNotBlank(reqVo.getLoginType()), LoginLog::getLoginType, reqVo.getLoginType())
                .eq(ObjectNull.isNotNull(reqVo.getStatus()), LoginLog::getStatus, reqVo.getStatus())
                .eq(StringUtils.isNotBlank(reqVo.getClientId()), LoginLog::getClientId, reqVo.getClientId())
                .between(ObjectUtil.isNotNull(reqVo.getBeginDate()) && ObjectUtil.isNotNull(reqVo.getEndDate())
                        , LoginLog::getOperateTime, reqVo.getBeginDate(), reqVo.getEndDate())
                .orderByDesc(LoginLog::getOperateTime));
        return R.ok(page);
    }

}
