package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.UserExtensionDto;
import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "userExtension")
public interface UserExtensionServiceApi {
    String PREFIX = "/api/user/extension";

    @GetMapping(PREFIX + "/query")
    R<List<UserExtensionDto>> query(@RequestParam("userIds") List<String> userIds, @RequestParam("type") String type);
}
