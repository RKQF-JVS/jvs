package cn.bctools.message.push.handler;

import cn.bctools.auth.api.api.AuthTenantConfigServiceApi;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.config.AliSmsConfig;
import cn.bctools.message.push.dto.config.BaseConfig;
import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.messagePush.ReceiversDto;
import cn.bctools.message.push.dto.vo.AliSmsTemplateVo;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.bctools.message.push.utils.JvsUserComponent;
import cn.bctools.message.push.utils.OtherUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class AliSmsHandler extends MessageHandler<AliSmsDto> {

    private final MessagePushHisService messagePushHisService;

    //    private final AliSmsConfig aliSmsConfig;
    private final JvsUserComponent jvsUserComponent;
    private final AuthTenantConfigServiceApi authTenantConfigServiceApi;

    @Override
    public void handle(AliSmsDto param) throws Exception {
        //生成批次号
        String batchNumber = OtherUtils.getUUID();
        if (!param.hasReceiver()) {
            MessagePushHis messagePushHis = new MessagePushHis()
                    .setBatchNumber(batchNumber)
                    .setPlatform(PlatformEnum.ALI_SMS)
                    .setMessageType(MessageTypeEnum.ALI_SMS)
                    .setMessageContent(JSON.toJSONString(param))
                    .setClientCode(param.getClientCode());
            messagePushHisService.save(messagePushHis);
            throw new BusinessException("没有检测到接收人配置");
        }

        jvsUserComponent.setAliSmsConfig(param.getDefinedReceivers());
        Map<String, Object> sms = authTenantConfigServiceApi.key(SysConstant.SMS, SysConstant.FRAME, TenantContextHolder.getTenantId()).getData();
        AliSmsConfig aliSmsConfig = BeanUtil.mapToBean(sms, AliSmsConfig.class, false, CopyOptions.create());

        Client aliSmsClient = createAliSmsClient(aliSmsConfig);
        //获取接收人
        List<ReceiversDto> definedReceivers = param.getDefinedReceivers();
        List<MessagePushHis> pushHisList = new ArrayList<>();

        definedReceivers.forEach(receiver -> {
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            sendSmsRequest.setPhoneNumbers(receiver.getReceiverConfig())
                    .setSignName(StringUtil.isNotBlank(param.getSignName()) ? param.getSignName() : aliSmsConfig.getSignature())
                    .setTemplateCode(param.getTemplateCode())
                    .setTemplateParam(param.getTemplateParam());

            MessagePushHis messagePushHis = new MessagePushHis()
                    .setBatchNumber(batchNumber)
                    .setPlatform(PlatformEnum.ALI_SMS)
                    .setMessageType(MessageTypeEnum.ALI_SMS)
                    .setMessageContent(JSON.toJSONString(param))
                    .setClientCode(param.getClientCode())
                    .setUserId(receiver.getUserId())
                    .setUserName(receiver.getUserName());

            RuntimeOptions runtime = new RuntimeOptions();
            try {
                SendSmsResponse sendSmsResponse = aliSmsClient.sendSmsWithOptions(sendSmsRequest, runtime);
                if (!sendSmsResponse.getBody().getCode().equals("OK")) {
                    throw new Exception(JSON.toJSONString(sendSmsResponse.getBody()));
                }
                messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
            } catch (Exception exception) {
                exception.printStackTrace();
                messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
                String eMessage = ExceptionUtil.getMessage(exception);
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
        Map<String, Object> sms = authTenantConfigServiceApi.key(SysConstant.SMS, SysConstant.FRAME, TenantContextHolder.getTenantId()).getData();
        AliSmsConfig aliSmsConfig = BeanUtil.mapToBean(sms, AliSmsConfig.class, false, CopyOptions.create());

        Client aliSmsClient = createAliSmsClient(aliSmsConfig);
        messagePushHisList.forEach(his -> {
            AliSmsDto hisDto = JSONUtil.toBean(his.getMessageContent(), AliSmsDto.class);
            hisDto.setSignName(aliSmsConfig.getSignature());
            hisDto.getDefinedReceivers().removeIf(e -> e.getUserId() == null || !e.getUserId().equals(his.getUserId()));
            if (!hisDto.getDefinedReceivers().isEmpty()) {
                ReceiversDto receiversDto = hisDto.getDefinedReceivers().stream().findFirst().get();
                SendSmsRequest sendSmsRequest = new SendSmsRequest();

                sendSmsRequest.setPhoneNumbers(receiversDto.getReceiverConfig())
                        .setSignName(StringUtil.isNotBlank(hisDto.getSignName()) ? hisDto.getSignName() : aliSmsConfig.getSignature())
                        .setTemplateCode(hisDto.getTemplateCode())
                        .setTemplateParam(hisDto.getTemplateParam());
                try {
                    SendSmsResponse sendSmsResponse = aliSmsClient.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
                    if (!sendSmsResponse.getBody().getCode().equals("OK")) {
                        throw new Exception(JSON.toJSONString(sendSmsResponse.getBody()));
                    }
                    log.info("批次号为{}接收人为{}的短信发送成功", batchNumber, his.getUserName());
                    his.setPushStatus(MessagePushStatusEnum.SUCCESS);
                } catch (Exception exception) {
                    log.error("发送消息失败", exception);
                    exception.printStackTrace();
                    his.setPushStatus(MessagePushStatusEnum.FAILED);
                    String eMessage = ExceptionUtil.getMessage(exception);
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

    public static Client createAliSmsClient(AliSmsConfig aliSmsConfig) {
        if (BaseConfig.hasNull(aliSmsConfig)) {
            throw new BusinessException("短信配置不完善");
        }
        Config config;
        Client client;
        try {
            config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(aliSmsConfig.getAccessKeyId())
                    // 您的AccessKey Secret
                    .setAccessKeySecret(aliSmsConfig.getAccessKeySecret());
            // 访问的域名 dysmsapi.aliyuncs.com
            // ps:Endpoint 是请求接口服务的网络域名
            // 如产品 ECS 的某个 Endpoint：ecs.cn-hangzhou.aliyuncs.com 每个产品都有其独立的 Endpoint，
            // 并且 Endpoint 与服务区域 RegionId 有关，不同地域可能是不同的 Endpoint
            config.endpoint = aliSmsConfig.getEndpoint();
            client = new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("阿里短信服务客户端创建失败");
        }
        return client;
    }

    /**
     * 再次发送检查
     *
     * @param pushHisList 发送历史
     * @param batchNumber 客户端
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
        AliSmsDto dto = JSON.parseObject(his.getMessageContent(), AliSmsDto.class);
        dto.getDefinedReceivers().removeIf(e -> e.getUserId() == null || !e.getUserId().equals(his.getUserId()));
        handle(dto);
    }

    /**
     * 查询当前账户下所有的短信模板 分页查询
     *
     * @param pageIndex 页码
     * @param pageSize  页数
     * @return
     */
    public List<AliSmsTemplateVo> getAllTemplate(Integer pageIndex, Integer pageSize) {
        List<AliSmsTemplateVo> result = new ArrayList<>();

        Map<String, Object> sms = authTenantConfigServiceApi.key(SysConstant.SMS, SysConstant.FRAME, TenantContextHolder.getTenantId()).getData();
        AliSmsConfig aliSmsConfig = BeanUtil.mapToBean(sms, AliSmsConfig.class, false, CopyOptions.create());

        Client aliSmsClient = createAliSmsClient(aliSmsConfig);

        QuerySmsTemplateListRequest querySmsTemplateListRequest = new QuerySmsTemplateListRequest();
        querySmsTemplateListRequest.setPageIndex(pageIndex);
        querySmsTemplateListRequest.setPageSize(pageSize);
        try {
            QuerySmsTemplateListResponse querySmsTemplateListResponse = aliSmsClient.querySmsTemplateList(querySmsTemplateListRequest);
            String successCode = "OK";
            if (successCode.equals(querySmsTemplateListResponse.getBody().code)) {
                List<QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList> smsTemplateList = querySmsTemplateListResponse.getBody().smsTemplateList;
                result = BeanUtil.copyToList(smsTemplateList, AliSmsTemplateVo.class);
            } else {
                log.error("同步消息模板错误" + JSONObject.toJSONString(querySmsTemplateListResponse.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
