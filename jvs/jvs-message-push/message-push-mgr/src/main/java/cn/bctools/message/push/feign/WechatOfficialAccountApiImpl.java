package cn.bctools.message.push.feign;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.api.WechatOfficialAccountApi;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.dto.vo.WxMpTemplateVo;
import cn.bctools.message.push.service.WechatOfficialAccountService;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "[Feign] 微信公众号接口")
public class WechatOfficialAccountApiImpl implements WechatOfficialAccountApi {

    private final WechatOfficialAccountService wechatOfficialAccountService;

//    private final UserComponent userComponent;

//    @Override
//    public R<Boolean> sendWebChatMpNewsMessage(NewsMessageDTO messageDto) {
//        return R.ok(wechatOfficialAccountService.sendWebChatMpNewsMessage(messageDto));
//    }
//
//    @Override
//    public R<Boolean> resendWebChatMpNewsMessage(String pushHisId) {
//        return R.ok(wechatOfficialAccountService.resendWebChatMpNewsMessage(pushHisId));
//    }
//
//    @Override
//    public R<Boolean> sendWebChatMpTextMessage(TextMessageDTO messageDto) {
//        return R.ok(wechatOfficialAccountService.sendWebChatMpTextMessage(messageDto));
//    }
//
//    @Override
//    public R<Boolean> resendWebChatMpTextMessage(String pushHisId) {
//        return R.ok(wechatOfficialAccountService.resendWebChatMpTextMessage(pushHisId));
//    }

    @Override
    public R<Boolean> sendWebChatTemplateMessage(TemplateMessageDTO messageDto) {
        wechatOfficialAccountService.sendWebChatTemplateMessage(messageDto, UserCurrentUtils.getCurrentUser());
        return R.ok(Boolean.TRUE);
    }

    @Override
    public R<Boolean> resendWebChatTemplateMessage(String pushHisId) {
        wechatOfficialAccountService.resendWebChatTemplateMessage(pushHisId);
        return R.ok(Boolean.TRUE);
    }

    @Override
    public List<WxMpTemplateVo> getAllPrivateTemplate() {
        return wechatOfficialAccountService.getAllPrivateTemplate();
    }
}
