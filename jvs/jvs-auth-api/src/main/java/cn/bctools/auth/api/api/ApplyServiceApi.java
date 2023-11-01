package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.ApplyDto;
import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * @author 
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "apply")
public interface ApplyServiceApi {

    String PREFIX = "/api/apply";

    /**
     * 获取所有的应用信息
     */
    @GetMapping("/all")
    R<List<ApplyDto>> all();

}
