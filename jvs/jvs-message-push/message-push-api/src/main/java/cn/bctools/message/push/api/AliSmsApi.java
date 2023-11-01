package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.vo.AliSmsTemplateVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发送短信
 */
@FeignClient(value = "message-push-mgr", contextId = "alibaba-sms")
public interface AliSmsApi {
    String prefix = "/alibaba/sms";

    /**
     * 阿里巴巴短信服务
     *
     * @param dto 消息数据
     * @return 执行状态
     */
    @PostMapping(prefix)
    @ApiOperation("发送短信")
    R<Boolean> sendAliSms(@RequestBody AliSmsDto dto);

    /**
     * 阿里巴巴短信服务
     *
     * @param pushHisId 消息数据
     * @return 执行状态
     */
    @GetMapping(prefix + "/resend")
    @ApiOperation("重新发送短信")
    R<Boolean> sendAliSms(String pushHisId);

    /**
     * 获取模板列表
     *
     * @return
     */
    @GetMapping(prefix + "/template/page")
    @ApiOperation("获取模板列表")
    List<AliSmsTemplateVo> getAllPrivateTemplate(@RequestParam("pageIndex") Integer pageIndex, @RequestParam("pageSize") Integer pageSize);
}
