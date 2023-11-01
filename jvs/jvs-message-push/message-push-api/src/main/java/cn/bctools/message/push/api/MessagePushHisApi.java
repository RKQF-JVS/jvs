package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.MessagePushHisDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 消息发送历史
 */
@FeignClient(value = "message-push-mgr",contextId = "push-his")
public interface MessagePushHisApi {

    String prefix = "/message/push/feign/his";

    @PostMapping(prefix)
    R<List<MessagePushHisDto>> getAllPushHis(@RequestBody MessagePushHisDto dto);

    @PostMapping(prefix+"/page")
    R<Page<MessagePushHisDto>> pagePushHis(@RequestParam("current")Long current, @RequestParam("size")Long size, @RequestBody MessagePushHisDto dto);

    @PostMapping(prefix+"/resend")
    R<Boolean> resend(@RequestParam("hisId") String hisId,@RequestParam("msgType") String msgType);

}
