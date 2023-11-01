package cn.bctools.message.push.handler.wechatofficialaccount;

import cn.bctools.auth.api.api.AuthTenantConfigServiceApi;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.config.WechatOfficialAccountConfig;
import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.ReceiversDto;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.TemplateMessageDTO;
import cn.bctools.message.push.dto.messagePush.wechatofficialaccount.WechatTemplateData;
import cn.bctools.message.push.dto.vo.WxMpTemplateVo;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.handler.MessageHandler;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.bctools.message.push.utils.JvsUserComponent;
import cn.bctools.message.push.utils.OtherUtils;
import cn.bctools.message.push.utils.SingletonUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 微信公众号文本消息handler
 **/

@Slf4j
@Component
@AllArgsConstructor
public class TemplateMessageHandler extends MessageHandler<TemplateMessageDTO> {

    private final MessagePushHisService messagePushHisService;
    private AuthTenantConfigServiceApi configServiceApi;
    private WechatOfficialAccountConfig config;
    private JvsUserComponent jvsUserComponent;

    @Override
    public void handle(TemplateMessageDTO param) {
        //生成批次号
        String batchNumber = OtherUtils.getUUID();
        if (!param.hasReceiver()) {
            MessagePushHis messagePushHis = new MessagePushHis()
                    .setBatchNumber(batchNumber)
                    .setPlatform(PlatformEnum.WECHAT_OFFICIAL_ACCOUNT)
                    .setMessageType(MessageTypeEnum.WECHAT_OFFICIAL_ACCOUNT_TEMPLATE)
                    .setMessageContent(JSON.toJSONString(param))
                    .setClientCode(param.getClientCode());
            messagePushHisService.save(messagePushHis);
            throw new BusinessException("没有检测到接收人配置");
        }
        //设置详细信息
        jvsUserComponent.setWxMpConfig(param.getDefinedReceivers());
        //创建service
        WxMpService wxService = this.createTemplateService();

        List<MessagePushHis> pushHisList = new ArrayList<>();
        param.getDefinedReceivers().forEach(receiverUser -> {
            //创建模板
            WxMpTemplateMessage wxMessageTemplate = this.createTemplate(param, receiverUser);
            MessagePushHis messagePushHis = new MessagePushHis()
                    .setMessageContent(JSON.toJSONString(param))
                    .setBatchNumber(batchNumber)
                    .setUserId(receiverUser.getUserId())
                    .setUserName(receiverUser.getUserName())
                    .setClientCode(param.getClientCode())
                    .setPlatform(PlatformEnum.WECHAT_OFFICIAL_ACCOUNT)
                    .setMessageType(MessageTypeEnum.WECHAT_OFFICIAL_ACCOUNT_TEMPLATE);
            try {
                wxService.getTemplateMsgService().sendTemplateMsg(wxMessageTemplate);
                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
            } catch (WxErrorException e) {
                e.printStackTrace();
                messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
                String eMessage = ExceptionUtil.getMessage(e);
                eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
                messagePushHis.setErrorMsg(eMessage);
            }
            pushHisList.add(messagePushHis);
        });
        messagePushHisService.saveBatch(pushHisList);
    }

    //    @Async
    public void handle(String batchNumber) {
        List<MessagePushHis> messagePushHisList = messagePushHisService.getNoSuccessHisList(batchNumber);
        this.send(messagePushHisList, batchNumber);
        this.verifyAgain(messagePushHisList, batchNumber, 2);
    }

    private void send(List<MessagePushHis> messagePushHisList, String batchNumber) {
        if (messagePushHisList.isEmpty()) {
            log.info("当前批次号{}未找到消息记录", batchNumber);
            return;
        }
        //创建service
        WxMpService wxService = this.createTemplateService();
        messagePushHisList.forEach(his -> {
            TemplateMessageDTO hisDto = JSONUtil.toBean(his.getMessageContent(), TemplateMessageDTO.class);
            hisDto.getDefinedReceivers().removeIf(e -> e.getUserId() == null || !e.getUserId().equals(his.getUserId()));

            if (!hisDto.getDefinedReceivers().isEmpty()) {
                ReceiversDto receiversDto = hisDto.getDefinedReceivers().stream().findFirst().get();
                log.info("微信公众号发送：用户属性：用户：{}，openId：{}，全属性：{}", receiversDto.getUserName(), receiversDto.getReceiverConfig(), receiversDto.toString());
                //创建模板
                WxMpTemplateMessage wxMessageTemplate = this.createTemplate(hisDto, receiversDto);
                try {
                    wxService.getTemplateMsgService().sendTemplateMsg(wxMessageTemplate);
                    his.setPushStatus(MessagePushStatusEnum.SUCCESS);
                    log.info("批次号为{}接收人为{}的微信公众号模板发送成功", batchNumber, his.getUserName());
                } catch (WxErrorException e) {
                    e.printStackTrace();
                    his.setPushStatus(MessagePushStatusEnum.FAILED);
                    String eMessage = ExceptionUtil.getMessage(e);
                    eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
                    his.setErrorMsg(eMessage);
                }
            } else {
                his.setErrorMsg("未设置接收人");
                his.setPushStatus(MessagePushStatusEnum.FAILED);
            }
        });
        messagePushHisService.updateBatchById(messagePushHisList);
    }

    private WxMpService createTemplateService() {

        //TODO 获取微信公众号的配置信息
        Map<String, Object> wechat_mp = configServiceApi.key(SysConstant.WECHAT_MP, SysConstant.FRAME, TenantContextHolder.getTenantId()).getData();
        config = BeanUtil.mapToBean(wechat_mp, WechatOfficialAccountConfig.class, false, CopyOptions.create());

        //获取当前租户的配置，而不是配置信息里面的配置
        WxMpService wxService = SingletonUtil.get(config.getAppId() + config.getAppSecret(), () -> {
            WxMpDefaultConfigImpl mpConfig = new WxMpDefaultConfigImpl();
            mpConfig.setAppId(config.getAppId());
            mpConfig.setSecret(config.getAppSecret());
            WxMpService wxService1 = new WxMpServiceImpl();
            wxService1.setWxMpConfigStorage(mpConfig);
            return wxService1;
        });
        return wxService;
    }

    /**
     * 创建模板
     *
     * @param param        微信公众号 dto
     * @param receiverUser 接收人
     * @return 模板
     */
    private WxMpTemplateMessage createTemplate(TemplateMessageDTO param, ReceiversDto receiverUser) {
        // 拼模板
        WxMpTemplateMessage wxMessageTemplate = new WxMpTemplateMessage();
        wxMessageTemplate.setTemplateId(param.getWechatTemplateId());
        wxMessageTemplate.setToUser(receiverUser.getReceiverConfig());

        wxMessageTemplate.setUrl(param.getUrl());

        // 小程序
        String miniAppId = param.getMiniAppId();
        if (StringUtils.isNotBlank(miniAppId)) {
            WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram(miniAppId, param.getMiniPagePath(), false);
            wxMessageTemplate.setMiniProgram(miniProgram);
        }

        // 模板变量
        List<WechatTemplateData> templateDataList = param.getTemplateDataList();
        for (WechatTemplateData wechatTemplateData : templateDataList) {
            String color = wechatTemplateData.getColor();
            color = StringUtils.isNoneBlank(color) && !color.contains("#") ? "#" + color : color; // 校正颜色
            wxMessageTemplate.addData(new WxMpTemplateData(wechatTemplateData.getName(), wechatTemplateData.getValue(), color));
        }
        return wxMessageTemplate;
    }

    /**
     * 再次发送检查
     *
     * @param pushHisList 发送历史
     * @param batchNumber 消息批次号
     */
    private void verifyAgain(List<MessagePushHis> pushHisList, String batchNumber, int cycleNum) {
        if (cycleNum <= 0) {
            return;
        }
        List<MessagePushHis> messagePushHisList = pushHisList.stream().filter(e -> MessagePushStatusEnum.FAILED.equals(e.getPushStatus())).collect(Collectors.toList());
        if (messagePushHisList.isEmpty()) {
            return;
        }
        this.send(messagePushHisList, batchNumber);
        cycleNum--;
        verifyAgain(messagePushHisList, batchNumber, cycleNum);
    }

    @Override
    public void resend(String pushHisId) throws Exception {
        MessagePushHis his = messagePushHisService.getById(pushHisId);
        TemplateMessageDTO dto = JSON.parseObject(his.getMessageContent(), TemplateMessageDTO.class);
        dto.getDefinedReceivers().removeIf(e -> e.getUserId() == null || !e.getUserId().equals(his.getUserId()));
        handle(dto);
    }

    public List<WxMpTemplateVo> getAllTemplate() {
        WxMpService templateService = this.createTemplateService();
        List<WxMpTemplateVo> result = new ArrayList<>();
        try {
            List<WxMpTemplate> allPrivateTemplate = templateService.getTemplateMsgService().getAllPrivateTemplate();
            if (!allPrivateTemplate.isEmpty()) {
                result = BeanUtil.copyToList(allPrivateTemplate, WxMpTemplateVo.class);
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return result;
    }
}
