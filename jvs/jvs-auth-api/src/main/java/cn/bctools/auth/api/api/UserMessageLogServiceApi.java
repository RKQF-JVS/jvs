package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.UserMessageLogDto;
import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description: 消息
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "messageLog")
public interface UserMessageLogServiceApi {

    String PREFIX = "/api/message/log";

    @PutMapping(PREFIX + "/save")
    R<Boolean> save(@Validated @RequestBody UserMessageLogDto userMessageLogDto);

    @PutMapping(PREFIX + "/save/batch")
    R<Boolean> saveBatch(@Validated @RequestBody List<UserMessageLogDto> userMessageLogDto);

}
