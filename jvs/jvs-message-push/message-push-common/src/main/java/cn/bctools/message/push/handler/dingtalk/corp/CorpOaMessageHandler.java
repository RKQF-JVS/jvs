//package cn.bctools.message.push.handler.dingtalk.corp;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.config.DingTalkCorpConfig;
//import cn.bctools.message.push.dto.messagePush.ReceiversDto;
//import cn.bctools.message.push.dto.messagePush.dingtalk.corp.OaMessageDTO;
//import cn.bctools.message.push.entity.MessageConfigDetail;
//import cn.bctools.message.push.entity.MessagePushHis;
//import cn.bctools.message.push.dto.enums.*;
//import cn.bctools.message.push.handler.MessageHandler;
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import cn.bctools.message.push.service.MessageConfigService;
//import cn.bctools.message.push.service.MessagePushHisService;
//import cn.bctools.message.push.utils.SingletonUtil;
//import cn.hutool.core.exceptions.ExceptionUtil;
//import com.alibaba.fastjson.JSON;
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
//import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
//import com.taobao.api.ApiException;
//import lombok.AllArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
///**
// * 钉钉工作通知文本类型消息处理器
// *
// **/
//@Component
//@AllArgsConstructor
//public class CorpOaMessageHandler extends MessageHandler<OaMessageDTO> {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final MessagePushHisService messagePushHisService;
//
//    @Override
//    public void handle(OaMessageDTO param) {
//        if(!param.hasReceiver()){
//            throw new BusinessException("没有检测到接收人配置");
//        }
//        //获取配置详情
//        MessageConfigDetail clientDetail = messageConfigDetailService.findByCode(param.getClientCode(),PlatformEnum.DING_TALK_CORP);
//        if(clientDetail==null){
//            throw new BusinessException("当前客户端未配置详细");
//        }
//        DingTalkCorpConfig config = JSON.parseObject(clientDetail.getConfigValue(), DingTalkCorpConfig.class);
//
//        //获取接收人
//        List<String> receiverUsers = param.getDefinedReceivers().stream().map(ReceiversDto::getReceiverConfig).collect(Collectors.toList());
//
//        DingTalkClient dingTalkClient = SingletonUtil.get("dinging-" + config.getAppKey() + config.getAppSecret(),
//                (SingletonUtil.Factory<DingTalkClient>) () -> new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2"));
//        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
//        request.setAgentId(Long.valueOf(config.getAgentId()));
//        request.setUseridList(String.join(",", receiverUsers));
//        request.setDeptIdList(param.getDeptIdList());
//        request.setToAllUser(param.isToAllUser());
//
//        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
//        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
//        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
//        msg.getOa().setMessageUrl(param.getMessageUrl());
//        msg.getOa().setPcMessageUrl(param.getPcMessageUrl());
//        msg.getOa().getHead().setText(param.getHeadText());
//        msg.getOa().getHead().setBgcolor(param.getHeadBgColor());
//        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
//        msg.getOa().getBody().setContent(param.getContent());
//        msg.getOa().getBody().setTitle(param.getBodyTitle());
//        msg.getOa().getBody().setAuthor(param.getAuthor());
//        msg.getOa().getBody().setFileCount(param.getFileCount());
//        msg.getOa().getBody().setImage(param.getImage());
//        OapiMessageCorpconversationAsyncsendV2Request.Rich rich = new OapiMessageCorpconversationAsyncsendV2Request.Rich();
//        rich.setNum(param.getRichNum());
//        rich.setUnit(param.getRichUnit());
//        msg.getOa().getBody().setRich(rich);
//
//        List<OaMessageDTO.BodyForm> bodyForms = param.getBodyForms();
//        List<OapiMessageCorpconversationAsyncsendV2Request.Form> forms = new ArrayList<>();
//        for (OaMessageDTO.BodyForm bodyForm : bodyForms) {
//            OapiMessageCorpconversationAsyncsendV2Request.Form form = new OapiMessageCorpconversationAsyncsendV2Request.Form();
//            form.setKey(bodyForm.getKey());
//            form.setValue(bodyForm.getValue());
//            forms.add(form);
//        }
//        msg.getOa().getBody().setForm(forms);
//
//        msg.getOa().setStatusBar(new OapiMessageCorpconversationAsyncsendV2Request.StatusBar());
//        msg.getOa().getStatusBar().setStatusBg(param.getStatusBarStatusBg());
//        msg.getOa().getStatusBar().setStatusValue(param.getStatusBarStatusValue());
//        msg.setMsgtype("oa");
//        request.setMsg(msg);
//
//        //生成批次号
//        String batchNumber = UUID.randomUUID().toString().replaceAll("-", "");
//
//        MessagePushHis messagePushHis = new MessagePushHis()
//                .setBatchNumber(batchNumber)
//                .setMessageContent(JSON.toJSONString(param))
//                .setPlatform(PlatformEnum.DING_TALK_CORP)
//                .setMessageType(MessageTypeEnum.DING_TALK_COPR_OA);
//
//        try {
//            OapiMessageCorpconversationAsyncsendV2Response rsp = dingTalkClient.execute(request, AccessTokenUtils.getAccessToken(config.getAppKey(), config.getAppSecret()));
//            if (!rsp.isSuccess()) {
//                throw new IllegalStateException(rsp.getBody());
//            }
//            messagePushHis.setPushStatus(MessagePushStatusEnum.SUCCESS);
//        } catch (ApiException e) {
//            e.printStackTrace();
//            messagePushHis.setPushStatus(MessagePushStatusEnum.FAILED);
//            String eMessage = ExceptionUtil.getMessage(e);
//            eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
//            messagePushHis.setErrorMsg(eMessage);
//        }
//        messagePushHisService.save(messagePushHis);
//    }
//
//    @Override
//    public void resend(String pushHisId) throws Exception {
//        MessagePushHis his = messagePushHisService.getById(pushHisId);
//        OaMessageDTO dto = JSON.parseObject(his.getMessageContent(),OaMessageDTO.class);
//        dto.getDefinedReceivers().removeIf(e -> e.getUserId()==null || !e.getUserId().equals(his.getUserId()));
//        handle(dto);
//    }
//}
