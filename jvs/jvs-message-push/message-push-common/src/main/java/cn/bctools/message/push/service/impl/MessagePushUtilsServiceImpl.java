package cn.bctools.message.push.service.impl;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.*;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.handler.AliSmsHandler;
import cn.bctools.message.push.handler.EmailMessageHandler;
import cn.bctools.message.push.handler.InsideNotificationHandler;
import cn.bctools.message.push.handler.wechatofficialaccount.TemplateMessageHandler;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.bctools.message.push.service.MessagePushUtilsService;
import cn.bctools.message.push.utils.JvsUserComponent;
import cn.bctools.message.push.utils.MessagePushHisUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessagePushUtilsServiceImpl implements MessagePushUtilsService {

    private final JvsUserComponent jvsUserComponent;
    private final MessagePushHisUtils messagePushHisUtils;
    private final MessagePushHisService messagePushHisService;

    private final AliSmsHandler aliSmsHandler;
    private final EmailMessageHandler emailMessageHandler;
    private final InsideNotificationHandler insideNotificationHandler;
    private final TemplateMessageHandler wxTemplateHandler;

    Map<String, UserDto> userMap;
    /**
     * 批量发送消息
     * @param batchSendDto
     * @param pushUser 发送人 如果是定时任务或者其他无法从系统中获取用户的情况一律为{id:-1,realName:系统通知}
     */
    @Async
    @Override
    public void batchSend(MessageBatchSendDto batchSendDto, UserDto pushUser) {
        TenantContextHolder.setTenantId(pushUser.getTenantId());
        String batchNumber = IdGenerator.getIdStr();
        if(batchSendDto.getComplementIs()){
            userMap = this.getUserMap(batchSendDto);
        }
        List<MessagePushHis> messagePushHisList = this.saveMessageHis(batchSendDto, pushUser, batchNumber);
        List<String> tenantIds = messagePushHisList.stream().map(MessagePushHis::getTenantId).distinct().collect(Collectors.toList());
        this.batchHandler(batchSendDto,batchNumber,tenantIds);
    }

    /**
     * 获取接收人集
     * @return
     */
    private Map<String, UserDto> getUserMap(MessageBatchSendDto batchSendDto){
        List<AliSmsDto> aliSmsDtoList = batchSendDto.getAliSmsDtoList();
        List<EmailMessageDto> emailDtoList = batchSendDto.getEmailDtoList();
        List<InsideNotificationDto> insideNotificationDtoList = batchSendDto.getInsideNotificationDtoList();
        List<TemplateMessageDTO> wxTemplateDtoList = batchSendDto.getWxTemplateDtoList();

        List<ReceiversDto> receiversIds = new ArrayList<>();
        if(ObjectUtil.isNotNull(aliSmsDtoList) && !aliSmsDtoList.isEmpty()){
            List<ReceiversDto> userIds = aliSmsDtoList.stream().map(AliSmsDto::getDefinedReceivers).flatMap(Collection::stream).collect(Collectors.toList());
            receiversIds.addAll(userIds);
        }
        if(ObjectUtil.isNotNull(emailDtoList) && !emailDtoList.isEmpty()){
            List<ReceiversDto> userIds = emailDtoList.stream().map(EmailMessageDto::getDefinedReceivers).flatMap(Collection::stream).collect(Collectors.toList());
            receiversIds.addAll(userIds);
        }
        if(ObjectUtil.isNotNull(insideNotificationDtoList) && !insideNotificationDtoList.isEmpty()){
            List<ReceiversDto> userIds = emailDtoList.stream().map(BaseMessage::getDefinedReceivers).flatMap(Collection::stream).collect(Collectors.toList());
            receiversIds.addAll(userIds);
        }
        if(ObjectUtil.isNotNull(wxTemplateDtoList) && !wxTemplateDtoList.isEmpty()){
            List<ReceiversDto> userIds = emailDtoList.stream().map(BaseMessage::getDefinedReceivers).flatMap(Collection::stream).collect(Collectors.toList());
            receiversIds.addAll(userIds);
        }
        List<UserDto> byIds = jvsUserComponent.findByIds(receiversIds);
        return byIds.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }

    /**
     * 保存消息发送记录
     *  aliSmsDtoList 短信
     *  emailDtoList 邮件
     *  insideNotificationDtoList 站内信
     *  wxTemplateDtoList 微信模板
     * @param pushUser 发送人 如果是定时任务或者其他无法从系统中获取用户的情况一律为{id:-1,realName:系统通知}
     * @param batchNumber 消息发送批次号
     */
    private List<MessagePushHis> saveMessageHis(MessageBatchSendDto batchSendDto,
                                UserDto pushUser,
                                String batchNumber){
        List<AliSmsDto> aliSmsDtoList = batchSendDto.getAliSmsDtoList();
        List<EmailMessageDto> emailDtoList = batchSendDto.getEmailDtoList();
        List<InsideNotificationDto> insideNotificationDtoList = batchSendDto.getInsideNotificationDtoList();
        List<TemplateMessageDTO> wxTemplateDtoList = batchSendDto.getWxTemplateDtoList();

        List<MessagePushHis> pushHisList = new ArrayList<>();
        if(ObjectUtil.isNotNull(aliSmsDtoList) && !aliSmsDtoList.isEmpty()){
            aliSmsDtoList.forEach(e -> {
                this.setReceiverDetail(batchSendDto.getComplementIs(),e.getDefinedReceivers(), "sms");
                List<MessagePushHis> messagePushHisList = this.generatePushHis(e.getDefinedReceivers(), e, PlatformEnum.ALI_SMS, MessageTypeEnum.ALI_SMS, pushUser, batchNumber);
                pushHisList.addAll(messagePushHisList);
            });
        }
        if(ObjectUtil.isNotNull(emailDtoList) && !emailDtoList.isEmpty()){
            emailDtoList.forEach(e -> {
                this.setReceiverDetail(batchSendDto.getComplementIs(),e.getDefinedReceivers(), "email");
                List<MessagePushHis> messagePushHisList = this.generatePushHis(e.getDefinedReceivers(), e,
                        PlatformEnum.EMAIL, MessageTypeEnum.EMAIL, pushUser, batchNumber);
                pushHisList.addAll(messagePushHisList);
            });
        }
        if(ObjectUtil.isNotNull(insideNotificationDtoList) && !insideNotificationDtoList.isEmpty()){
            insideNotificationDtoList.forEach(e -> {
                this.setReceiverDetail(batchSendDto.getComplementIs(),e.getDefinedReceivers(), "inside");
                messagePushHisUtils.saveNoticeMessage(e.getDefinedReceivers(),JSONUtil.parseObj(e),pushUser,batchNumber);
                List<MessagePushHis> messagePushHisList = this.generatePushHis(e.getDefinedReceivers(), e,
                        PlatformEnum.INSIDE_NOTIFICATION, MessageTypeEnum.INSIDE_NOTIFICATION, pushUser, batchNumber);
                pushHisList.addAll(messagePushHisList);
            });
        }
        if(ObjectUtil.isNotNull(wxTemplateDtoList) && !wxTemplateDtoList.isEmpty()){
            wxTemplateDtoList.forEach(e -> {
                this.setReceiverDetail(batchSendDto.getComplementIs(),e.getDefinedReceivers(), "wx");
                List<MessagePushHis> messagePushHisList = this.generatePushHis(e.getDefinedReceivers(), e,
                        PlatformEnum.WECHAT_OFFICIAL_ACCOUNT, MessageTypeEnum.WECHAT_OFFICIAL_ACCOUNT_TEMPLATE, pushUser, batchNumber);
                pushHisList.addAll(messagePushHisList);
            });
        }
        messagePushHisService.saveBatch(pushHisList);
        return pushHisList;
    }

    /**
     * 生成消息发送记录
     * @param receiversDtos 接收人
     * @param message 消息
     * @param platformEnum 平台
     * @param messageTypeEnum 消息类型
     * @param pushUser 发送人 如果是定时任务或者其他无法从系统中获取用户的情况一律为{id:-1,realName:系统通知}
     * @param batchNumber 消息发送批次号
     * @return
     */
    List<MessagePushHis> generatePushHis(List<ReceiversDto> receiversDtos,
                                         Object message,
                                         PlatformEnum platformEnum,
                                         MessageTypeEnum messageTypeEnum ,
                                         UserDto pushUser,
                                         String batchNumber){
        List<MessagePushHis> pushHisList = new ArrayList<>();
        JSONObject dto = JSONUtil.parseObj(message);
        receiversDtos.forEach(receiver -> {
            MessagePushHis messagePushHis = new MessagePushHis()
                    .setBatchNumber(batchNumber)
                    .setPlatform(platformEnum)
                    .setMessageType(messageTypeEnum)
                    .setMessageContent(JSON.toJSONString(dto))
                    .setClientCode( dto.getStr("clientCode"))
                    .setUserId(receiver.getUserId())
                    .setUserName(receiver.getUserName());
            messagePushHis.setCreateById(pushUser.getId());
            messagePushHis.setCreateBy(pushUser.getRealName());
            messagePushHis.setUpdateBy(pushUser.getRealName());
            String tenantId = messagePushHisUtils.getTenantId(receiver);
            messagePushHis.setTenantId(tenantId);
            if(PlatformEnum.INSIDE_NOTIFICATION.equals(platformEnum)){
                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
            }
            pushHisList.add(messagePushHis);
        });
        return pushHisList;
    }

    /**
     * 消息处理 - 正式开始发送消息
     * @param batchSendDto
     * @param batchNumber
     */
    void batchHandler(MessageBatchSendDto batchSendDto, String batchNumber,List<String> tenantIds){
        for (String tenantId : tenantIds) {
            TenantContextHolder.setTenantId(tenantId);
            if(Optional.of(batchSendDto).map(MessageBatchSendDto::getAliSmsDtoList).filter(e -> !e.isEmpty()).isPresent()){
                aliSmsHandler.handle(batchNumber);
            }
            if(Optional.of(batchSendDto).map(MessageBatchSendDto::getEmailDtoList).filter(e -> !e.isEmpty()).isPresent()){
                emailMessageHandler.handle(batchNumber);
            }
            if(Optional.of(batchSendDto).map(MessageBatchSendDto::getInsideNotificationDtoList).filter(e -> !e.isEmpty()).isPresent()){
                insideNotificationHandler.handle(batchNumber);
            }
            if(Optional.of(batchSendDto).map(MessageBatchSendDto::getWxTemplateDtoList).filter(e -> !e.isEmpty()).isPresent()){
                wxTemplateHandler.handle(batchNumber);
            }
        }
    }

    /**
     * 补充接收人信息
     * @param receiversDtoList
     * @param type
     */
    private void setReceiverDetail(Boolean complementIs,List<ReceiversDto> receiversDtoList,String type){
        if(complementIs){
            receiversDtoList.forEach(o -> {
                UserDto userDto = userMap.get(o.getUserId());
                String receiverConfig;
                switch (type){
                    case "wx":
                        String openId = "-1";
                        if(ObjectNull.isNotNull(userDto.getExceptions()) && userDto.getExceptions().containsKey("WECHAT_MP")){
                            JSONObject wechat_mp = JSONUtil.parseObj(userDto.getExceptions().get("WECHAT_MP"));
                            openId = wechat_mp.getStr("openId");
                        }
                        receiverConfig = openId;
                        break;
                    case "inside":
                        receiverConfig = userDto.getId();
                        break;
                    case "email":
                        receiverConfig = userDto.getEmail();
                        break;
                    case "sms":
                        receiverConfig = userDto.getPhone();
                        break;
                    default:
                        receiverConfig = "-1";
                }

                o.setUserName(userDto.getRealName())
                        .setReceiverConfig(receiverConfig)
                        .setTenantId(userDto.getTenantId());
            });
        }
    }
}
