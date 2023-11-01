package cn.bctools.message.push.service.impl;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.dto.vo.WxMpTemplateVo;
import cn.bctools.message.push.handler.wechatofficialaccount.TemplateMessageHandler;
import cn.bctools.message.push.service.WechatOfficialAccountService;
import cn.bctools.message.push.utils.MessagePushHisUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WechatOfficialAccountServiceImpl implements WechatOfficialAccountService {

    //    private final MpNewsMessageHandler newsMessageHandler;
//    private final MpTextMessageHandler textMessageHandler;
    private final TemplateMessageHandler templateMessageHandler;
    private final MessagePushHisUtils messagePushHisUtils;

//    @Override
//    public boolean sendWebChatMpNewsMessage(NewsMessageDTO messageDto) {
//        try {
//            newsMessageHandler.handle(messageDto);
//            return Boolean.TRUE;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Boolean.FALSE;
//        }
//    }
//
//    @Override
//    public boolean sendWebChatMpTextMessage(TextMessageDTO messageDto) {
//        try {
//            textMessageHandler.handle(messageDto);
//            return Boolean.TRUE;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Boolean.FALSE;
//        }
//    }

    @Override
    public void sendWebChatTemplateMessage(TemplateMessageDTO messageDto, UserDto pushUser) {
        try {
            //设置租户id
            TenantContextHolder.setTenantId(Optional.ofNullable(pushUser).map(UserDto::getTenantId).orElse("1"));
            String batchNumber = messagePushHisUtils.saveHis(messageDto, pushUser, PlatformEnum.WECHAT_OFFICIAL_ACCOUNT, MessageTypeEnum.WECHAT_OFFICIAL_ACCOUNT_TEMPLATE);
            templateMessageHandler.handle(batchNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    @Override
//    public boolean resendWebChatMpNewsMessage(String pushHisId) {
//        try {
//            newsMessageHandler.resend(pushHisId);
//            return Boolean.TRUE;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Boolean.FALSE;
//        }
//    }
//
//    @Override
//    public boolean resendWebChatMpTextMessage(String pushHisId) {
//        try {
//            textMessageHandler.resend(pushHisId);
//            return Boolean.TRUE;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Boolean.FALSE;
//        }
//    }

    @Override
    public void resendWebChatTemplateMessage(String pushHisId) {
        try {
            templateMessageHandler.resend(pushHisId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WxMpTemplateVo> getAllPrivateTemplate() {
        return templateMessageHandler.getAllTemplate();
    }
}
