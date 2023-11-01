package cn.bctools.message.push.service;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.dto.vo.WxMpTemplateVo;

import java.util.List;

public interface WechatOfficialAccountService {

//    boolean sendWebChatMpNewsMessage(NewsMessageDTO messageDto);
//
//    boolean sendWebChatMpTextMessage(TextMessageDTO messageDto);

    void sendWebChatTemplateMessage(TemplateMessageDTO messageDto, UserDto pushUser);

//    boolean resendWebChatMpNewsMessage(String pushHisId);
//
//    boolean resendWebChatMpTextMessage(String pushHisId);

    void resendWebChatTemplateMessage(String pushHisId);

    List<WxMpTemplateVo> getAllPrivateTemplate();
}
