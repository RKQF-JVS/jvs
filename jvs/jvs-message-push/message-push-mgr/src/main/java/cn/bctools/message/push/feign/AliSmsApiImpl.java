package cn.bctools.message.push.feign;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.api.AliSmsApi;
import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.vo.AliSmsTemplateVo;
import cn.bctools.message.push.service.AliSmsService;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "[Feign] 短信接口")
public class AliSmsApiImpl implements AliSmsApi {

    private final AliSmsService aliSmsService;
//    private final UserComponent userComponent;

    @Override
    public R<Boolean> sendAliSms(AliSmsDto dto) {
        aliSmsService.send(dto, UserCurrentUtils.getCurrentUser());
        return R.ok(Boolean.TRUE);
    }

    @Override
    public R<Boolean> sendAliSms(String pushHisId) {
        aliSmsService.resend(pushHisId);
        return R.ok(Boolean.TRUE);
    }

    @Override
    public List<AliSmsTemplateVo> getAllPrivateTemplate(Integer pageIndex, Integer pageSize) {
        return aliSmsService.getAllPrivateTemplate(pageIndex, pageSize);
    }
}
