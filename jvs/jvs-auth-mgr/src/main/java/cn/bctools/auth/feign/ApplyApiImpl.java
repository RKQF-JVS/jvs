package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.ApplyServiceApi;
import cn.bctools.auth.api.dto.ApplyDto;
import cn.bctools.auth.service.ApplyService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.gateway.entity.Apply;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "[Feign]应用接口")
public class ApplyApiImpl implements ApplyServiceApi {
    ApplyService applyService;

    @Override
    public R<List<ApplyDto>> all() {
        List<Apply> list = applyService.list();
        return R.ok(BeanCopyUtil.copys(list, ApplyDto.class));
    }
}
