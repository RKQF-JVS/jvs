package cn.bctools.message.push.service.impl;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.vo.AliSmsTemplateVo;
import cn.bctools.message.push.handler.AliSmsHandler;
import cn.bctools.message.push.service.AliSmsService;
import cn.bctools.message.push.utils.MessagePushHisUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AliSmsServiceImpl implements AliSmsService {

    private final AliSmsHandler aliSmsHandler;
    private final MessagePushHisUtils messagePushHisUtils;

    @Override
    public void send(AliSmsDto dto, UserDto pushUser) {
        try {
            //设置租户id
            TenantContextHolder.setTenantId(Optional.ofNullable(pushUser).map(UserDto::getTenantId).orElse("1"));
            //储存消息日志
            String batchNumber = messagePushHisUtils.saveHis(dto, pushUser, PlatformEnum.ALI_SMS, MessageTypeEnum.ALI_SMS);
            aliSmsHandler.handle(batchNumber);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void resend(String pushHisId) {
        try {
            aliSmsHandler.resend(pushHisId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AliSmsTemplateVo> getAllPrivateTemplate(Integer pageIndex, Integer pageSize) {
        return aliSmsHandler.getAllTemplate(pageIndex, pageSize);
    }
}
