package cn.bctools.auth.contoller;

import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.service.ApplyService;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.TenantPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@RestController
@AllArgsConstructor
public class ApplyAuthController {

    private final SysConfigService sysConfigService;
    private final ApplyService applyService;

    @GetMapping("/api/domain")
    public R<Object> domain(@RequestParam(value = "client_id", required = false) String clientId) {
        //根据上下文获取租户
        String tenantId = TenantContextHolder.getTenantId();
        Map<String, Object> config = sysConfigService.key(tenantId, clientId, SysConfigTypeEnum.BASIC);
        if (MapUtils.isNotEmpty(config)) {
            return R.ok(config);
        } else {
            //没有匹配到租户,则直接返回终端的配置
            Apply apply = applyService.getOne(new LambdaQueryWrapper<Apply>().eq(Apply::getAppKey, clientId));
            if (ObjectNull.isNotNull(apply)) {
                TenantPo copy = BeanCopyUtil.copy(apply, TenantPo.class);
                copy.setDescMsg(apply.getDescribes());
                return R.ok(copy);
            } else {
                return null;
            }
        }
    }

}
