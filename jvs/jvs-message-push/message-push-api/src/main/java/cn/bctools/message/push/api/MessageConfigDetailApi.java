package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(value = "message-push-mgr",contextId = "message-detail")
public interface MessageConfigDetailApi {
    String prefix = "/message/detail";

    @GetMapping(prefix+"/check")
    R<Map<PlatformEnum,Boolean>> getEffectivePlatForm();
}
