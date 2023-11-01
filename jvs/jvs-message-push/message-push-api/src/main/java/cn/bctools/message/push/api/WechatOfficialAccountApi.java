package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.dto.vo.WxMpTemplateVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 微信公众号
 */
@FeignClient(value = "message-push-mgr", contextId = "webchat-official-account")
public interface WechatOfficialAccountApi {
    String prefix = "/webchat/official/account";

//    /**
//     * 微信公众号图文消息
//     * @param messageDto 消息数据
//     * @return 执行信息
//     */
//    @PostMapping(prefix+"/news")
//    @ApiOperation("发送微信公众号图文消息")
//    R<Boolean> sendWebChatMpNewsMessage(@RequestBody NewsMessageDTO messageDto);
//
//    /**
//     * 微信公众号图文消息
//     * @param pushHisId 消息历史id
//     * @return 执行信息
//     */
//    @GetMapping(prefix+"/news/resend")
//    @ApiOperation("重发送微信公众号图文消息")
//    R<Boolean> resendWebChatMpNewsMessage(String pushHisId);
//
//    /**
//     * 微信公众号客户消息
//     * @param messageDto 消息数据
//     * @return 执行信息
//     */
//    @PostMapping(prefix+"/text")
//    @ApiOperation("发送微信公众号客户消息")
//    R<Boolean> sendWebChatMpTextMessage(@RequestBody TextMessageDTO messageDto);
//
//    /**
//     * 微信公众号客户消息
//     * @param pushHisId 消息历史id
//     * @return 执行信息
//     */
//    @GetMapping(prefix+"/text/resend")
//    R<Boolean> resendWebChatMpTextMessage(String pushHisId);

    /**
     * 微信公众号模板消息
     *
     * @param messageDto 消息数据
     * @return 执行信息
     */
    @PostMapping(prefix + "/template")
    @ApiOperation("发送微信公众号模板消息")
    R<Boolean> sendWebChatTemplateMessage(@RequestBody TemplateMessageDTO messageDto);

    /**
     * 微信公众号模板消息
     *
     * @param pushHisId 消息历史id
     * @return 执行信息
     */
    @GetMapping(prefix + "/template/resend")
    @ApiOperation("重新发送微信公众号模板消息")
    R<Boolean> resendWebChatTemplateMessage(String pushHisId);

    /**
     * 获取当前公众号的模板列表
     *
     * @return
     */
    @GetMapping(prefix + "/template/all")
    @ApiOperation("获取当前公众号的模板列表")
    List<WxMpTemplateVo> getAllPrivateTemplate();
}
