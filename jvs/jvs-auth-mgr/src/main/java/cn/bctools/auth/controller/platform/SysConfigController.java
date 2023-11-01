package cn.bctools.auth.controller.platform;


import cn.bctools.auth.entity.SysConfig;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.service.impl.SysConfigServiceImpl;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 
 */
@Api(tags = "独立的其它配置")
@RestController
@AllArgsConstructor
@RequestMapping("/sys/config")
public class SysConfigController {

    private final SysConfigServiceImpl sysConfigService;

    @Log
    @PostMapping("/{tenantId}/{appId}/{type}")
    @ApiOperation(value = "保存配置信息")
    public R<SysConfig> edit(@PathVariable String tenantId, @PathVariable String appId, @PathVariable SysConfigTypeEnum type, @RequestBody Map<String, Object> body) {
        if (MapUtils.isEmpty(body)) {
            throw new BusinessException("请完善配置");
        }
        //如果是超级管理员，需要校验，如果不是，只能是自己修改自己的
        if (!UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            if (!UserCurrentUtils.getCurrentUser().getTenantId().equals(tenantId)) {
                return R.failed("非法操作");
            }
        }
        sysConfigService.saveConfig(tenantId, appId, type, body);
        return R.ok();
    }

    @Log
    @GetMapping("/tenant/app/{tenantId}")
    @ApiOperation(value = "获取租户应用配置")
    public R<Map<String, Map<String, Object>>> getTenantApp(@PathVariable String tenantId) {
        //如果是超级管理员，需要校验，如果不是，只能是自己修改自己的
        if (!UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            if (UserCurrentUtils.getCurrentUser().getTenantId().equals(tenantId)) {
                return R.failed("非法操作");
            }
        }
        return R.ok(sysConfigService.getTenantApp(tenantId));
    }

    @Log
    @GetMapping("/tenant/app/type/{type}")
    @ApiOperation(value = "获取租户应用配置")
    public R getTenantValue(@PathVariable SysConfigTypeEnum type) {
        Map<String, Object> collect = sysConfigService.list(Wrappers.query(new SysConfig().setJvsTenantId(UserCurrentUtils.getCurrentUser().getTenantId()).setType(type)))
                .stream()
                .collect(Collectors.toMap(SysConfig::getName, SysConfig::getContent));
        return R.ok(collect);
    }

}
