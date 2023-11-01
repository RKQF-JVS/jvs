package cn.bctools.message.push.handler.wechatwork.agent;

import cn.bctools.auth.api.api.AuthTenantConfigServiceApi;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.config.WechatWorkAgentConfig;
import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.wechatwork.agent.WeTextMessageDTO;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.handler.MessageHandler;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.bctools.message.push.utils.SingletonUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 企业微信文本消息handler
 **/
@Component
@AllArgsConstructor
public class AgentTextMessageHandler extends MessageHandler<WeTextMessageDTO> {

    //    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
    private final MessagePushHisService messagePushHisService;
    private final AuthTenantConfigServiceApi authTenantConfigServiceApi;

    @Override
    public void handle(WeTextMessageDTO param) {
        if (!param.hasReceiver()) {
            throw new BusinessException("没有检测到接收人配置");
        }
        //获取配置详情

        Map<String, Object> enterprise = authTenantConfigServiceApi.key(SysConstant.WX_ENTERPRISE, SysConstant.FRAME, TenantContextHolder.getTenantId()).getData();
        WechatWorkAgentConfig config = new WechatWorkAgentConfig();
        config.setAgentId(Integer.valueOf(String.valueOf(enterprise.get("agentId"))));
        config.setCorpId(String.valueOf(enterprise.get("appId")));
        config.setSecret(String.valueOf(enterprise.get("appSecret")));

//        WechatWorkAgentConfig config = JSON.parseObject(clientDetail.getConfigValue(), WechatWorkAgentConfig.class);

        WxCpServiceImpl wxCpService = SingletonUtil.get(config.getCorpId() + config.getSecret() + config.getAgentId(), () -> {
            WxCpDefaultConfigImpl cpConfig = new WxCpDefaultConfigImpl();
            cpConfig.setCorpId(config.getCorpId());
            cpConfig.setCorpSecret(config.getSecret());
            cpConfig.setAgentId(config.getAgentId());
            WxCpServiceImpl wxCpService1 = new WxCpServiceImpl();
            wxCpService1.setWxCpConfigStorage(cpConfig);
            return wxCpService1;
        });

        //生成批次号
        String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");

        param.getDefinedReceivers().forEach(e -> {
            MessagePushHis messagePushHis = new MessagePushHis();
            messagePushHis.setMessageContent(JSON.toJSONString(param))
                    .setBatchNumber(batchNumber)
                    .setUserId(e.getUserId())
                    .setUserName(e.getUserName())
                    .setClientCode(param.getClientCode())
                    .setPlatform(PlatformEnum.WECHAT_WORK_AGENT)
                    .setMessageType(MessageTypeEnum.WECHAT_WORK_AGENT_TEXT);

            WxCpMessage message = WxCpMessage.TEXT()
                    .agentId(config.getAgentId())
                    .toUser(e.getReceiverConfig())
                    .content(param.getContent())
                    .toParty(param.getToParty())
                    .toTag(param.getToTag())
                    .build();
            try {
                wxCpService.getMessageService().send(message);
                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
            } catch (Exception exception) {
                messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
                String eMessage = ExceptionUtil.getMessage(exception);
                eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
                messagePushHis.setErrorMsg(eMessage);
            }
            messagePushHisService.save(messagePushHis);
        });
    }

    @Override
    public void resend(String pushHisId) throws Exception {
        MessagePushHis his = messagePushHisService.getById(pushHisId);
        WeTextMessageDTO dto = JSON.parseObject(his.getMessageContent(), WeTextMessageDTO.class);
        dto.getDefinedReceivers().removeIf(e -> e.getUserId() == null || !e.getUserId().equals(his.getUserId()));
        handle(dto);
    }
}
