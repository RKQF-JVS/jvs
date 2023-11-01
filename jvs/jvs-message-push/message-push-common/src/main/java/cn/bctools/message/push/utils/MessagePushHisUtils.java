package cn.bctools.message.push.utils;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.enums.InsideNotificationTypeEnum;
import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.ReceiversDto;
import cn.bctools.message.push.entity.InsideNotice;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.service.InsideNoticeService;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class MessagePushHisUtils {
    private final MessagePushHisService messagePushHisService;
    private final JvsUserComponent jvsUserComponent;
    private final InsideNoticeService insideNoticeService;

    /**
     * 储存消息发送日志
     * @param params 消息数据
     * @param platformEnum 平台类型
     * @param messageTypeEnum 消息类型
     * @return 消息批次号
     */
    public String saveHis(Object params, UserDto pushUser, PlatformEnum platformEnum, MessageTypeEnum messageTypeEnum){
        String batchNumber = IdGenerator.getIdStr();
        JSONObject dto = JSONUtil.parseObj(params);
        JSONArray definedReceivers = dto.getJSONArray("definedReceivers");
        if(ObjectNull.isNull(definedReceivers)){
            throw new BusinessException("未设置接收人");
        }
        List<ReceiversDto> receiversDtos = JSONUtil.toList(definedReceivers, ReceiversDto.class);

        if(receiversDtos.isEmpty()){
            throw new BusinessException("未设置接收人");
        }
        if(PlatformEnum.EMAIL.equals(platformEnum)){
            jvsUserComponent.setEmailConfig(receiversDtos);
        }
        if(PlatformEnum.ALI_SMS.equals(platformEnum)){
            jvsUserComponent.setAliSmsConfig(receiversDtos);
        }
        if(PlatformEnum.WECHAT_OFFICIAL_ACCOUNT.equals(platformEnum)){
            jvsUserComponent.setWxMpConfig(receiversDtos);
        }
        if(PlatformEnum.INSIDE_NOTIFICATION.equals(platformEnum)){
            jvsUserComponent.setInsideNoticeConfig(receiversDtos);
            this.saveNoticeMessage(receiversDtos,dto,pushUser,batchNumber);
        }

        dto.set("definedReceivers",receiversDtos);
        List<MessagePushHis> pushHisList = new ArrayList<>();
        receiversDtos.forEach(receiver -> {
            MessagePushHis messagePushHis = new MessagePushHis()
                    .setBatchNumber(batchNumber)
                    .setPlatform(platformEnum)
                    .setMessageType(messageTypeEnum)
                    .setMessageContent(JSON.toJSONString(dto))
                    .setClientCode( dto.getStr("clientCode"))
                    .setUserId(receiver.getUserId())
                    .setUserName(receiver.getUserName())
                    .setTenantId(pushUser.getTenantId());
            messagePushHis.setCreateById(pushUser.getId());
            messagePushHis.setCreateBy(pushUser.getRealName());
            messagePushHis.setUpdateBy(pushUser.getRealName());
            if(PlatformEnum.INSIDE_NOTIFICATION.equals(platformEnum)){
                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
            }
            log.info("微信公众号用户属性：用户：{}，openId：{}，全属性：{}",receiver.getUserName(),receiver.getReceiverConfig(),receiver.toString());
            pushHisList.add(messagePushHis);
        });
        messagePushHisService.saveBatch(pushHisList);
        return batchNumber;
    }

    /**
     * 保存站内信记录
     * @param receiversDtos 接收人
     * @param dto 消息内容
     * @param pushUser 发送人
     * @param batchNumber 消息发送批次号
     */
    public void saveNoticeMessage(List<ReceiversDto> receiversDtos,JSONObject dto, UserDto pushUser,String batchNumber){
        List<InsideNotice> noticeList = new ArrayList<>();
        InsideNotificationTypeEnum largeCategories = Optional.ofNullable(dto.getStr("largeCategories"))
                .map(InsideNotificationTypeEnum::valueOf)
                .orElse(InsideNotificationTypeEnum.notice);
        //消息头像
        String noticeAvatar = InsideNotificationTypeEnum.notice.equals(largeCategories)?pushUser.getHeadImg():pushUser.getTenant().getIcon();
        receiversDtos.forEach(receiversDto -> {
            String tenantId = this.getTenantId(receiversDto);
            InsideNotice insideNotice = new InsideNotice()
                    .setReadIs(Boolean.FALSE)
                    .setUserId(receiversDto.getUserId())
                    .setUserName(receiversDto.getUserName())
                    .setMsgContent(dto.getStr("content"))
                    //消息批次号
                    .setBatchNumber(batchNumber)
                    //终端key
                    .setClientCode(dto.getStr("clientCode"))
                    //大类
                    .setLargeCategories(largeCategories)
                    //小类
                    .setSubClass(dto.getStr("subClass"))
                    //回调地址
                    .setCallBackUrl(dto.getStr("callBackUrl"))
                    //消息头像
                    .setNoticeAvatar(noticeAvatar)
                    //设置租户id
                    .setTenantId(tenantId);
            insideNotice.setCreateById(pushUser.getId());
            insideNotice.setCreateBy(pushUser.getRealName());
            insideNotice.setUpdateBy(pushUser.getRealName());
            noticeList.add(insideNotice);
        });
        insideNoticeService.saveBatch(noticeList);
    }

    public String getTenantId(ReceiversDto receiversDto){
        return StrUtil.isBlank(TenantContextHolder.getTenantId())||
                ObjectUtil.equals(TenantContextHolder.getTenantId(), JvsUserComponent.DEFAULT_TENANT_ID)
                ?receiversDto.getTenantId():TenantContextHolder.getTenantId();
    }
}
