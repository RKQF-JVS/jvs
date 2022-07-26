package cn.bctools.auth.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.log.mapper.SysLogDao;
import cn.bctools.log.po.LogPo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * 错误请求
 *
 * @author guojing
 */
@Api(tags = "错误请求")
@RestController
@AllArgsConstructor
@RequestMapping("/request")
public class SysRequestController {

    private final SysLogDao sysLogDao;

    @Log(back = false)
    @ApiOperation("分页错误请求")
    @GetMapping("/page")
    public R<IPage<LogPo>> getDictPage(Page<LogPo> page, LogPo dto) {
        LogPo logPo = new LogPo()
                .setTid(dto.getTid())
                .setFunctionName(dto.getFunctionName())
                .setUserName(dto.getUserName())
                .setBusinessName(dto.getBusinessName());
        if (ObjectNull.isNull(dto.getStartTime(), dto.getEndTime())) {
            return R.failed("数据量大请必须使用时间查询过滤");
        }
        if (Duration.between(dto.getStartTime(), dto.getEndTime()).toDays() > 7) {
            return R.failed("查询时间不能超过7天");
        }
        return R.ok(sysLogDao.selectPage(page, Wrappers.lambdaQuery(logPo)
                .between(LogPo::getStartTime, dto.getStartTime(), dto.getEndTime())
                .orderByDesc(LogPo::getCreateDate)));
    }

}
