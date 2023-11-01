package cn.bctools.sms.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.sms.config.AliSmsConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
public class SmsSendUtils {

    public static final String ALIYUN_SMS_SUCCESS_CODE = "OK";
    /**
     * 阿里  初始化ascClient需要的几个参数
     * 短信API产品名称（短信产品名固定，无需修改）
     */
    public static final String PRODUCT = "Dysmsapi";
    /**
     * 短信API产品域名（接口地址固定，无需修改）
     */
    public static final String DOMAIN = "dysmsapi.aliyuncs.com";

    public static final String WRONG_KEY = "InvalidAccessKeyId.NotFound";
    public static final String WRONG_SECRET = "SDK.InvalidAccessKeySecret";

    private static final String LEFT = "${";
    private static final String RIGHT = "}";

    /**
     * 替换模板内容  此方法只会在新增租户的时候用来下发租户注册成功的短信模板
     *
     * @param smsTemplate 模板
     * @param mappings    变量
     * @return
     */
    public static String replace(SmsTemplate smsTemplate, Map<String, Object> mappings) {
        String content = smsTemplate.getContent();
        for (String key : mappings.keySet()) {
            String keyword = LEFT + key + RIGHT;
            if (content.contains(keyword)) {
                content = content.replace(keyword, mappings.get(key).toString());
            }
        }
        return content;
    }

    public static String smsCode() {
        String random = (int) ((Math.random() * 9 + 1) * 100000) + "";
        log.debug("创建的验证码为：{}", random);
        return random;
    }

    /**
     * 阿里云短信发送
     *
     * @param templateCode 模板code
     * @param phones       手机号
     * @param variables    变量
     * @return 阿里云数据返回
     */
    public static Object aliImpl(String templateCode, List<String> phones, Map<String, String> variables) {
        AliSmsConfig bean = SpringContextUtil.getBean(AliSmsConfig.class);
        return aliImpl(bean, templateCode, phones, variables);
    }

    public static Object aliImpl(AliSmsConfig bean, String templateCode, List<String> phones, Map<String, String> variables) {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        String accessKeyId = bean.getAccessKeyId();
        String accessKeySecret = bean.getAccessKeySecret();
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", PRODUCT, DOMAIN);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setSysMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,
        // 验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
        request.setPhoneNumbers(String.join(",", phones));
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(bean.getSignature());
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        String templateParam = JSONObject.toJSONString(variables);
        request.setTemplateParam(templateParam);
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = null;
        try {
            log.info("短信发送参数为:{}", JSONObject.toJSONString(request));
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("阿里云短信,初始化短信客户端异常", e);
            //初始化短信客户端异常
            if (e.getMessage().startsWith(WRONG_KEY)) {
                throw new BusinessException("无效的访问key(AccessKeyId)");
            }
            if (e.getMessage().startsWith(WRONG_SECRET)) {
                throw new BusinessException("无效的访问秘钥(AccessKeySecret)");
            }
        } catch (Exception e) {
            log.error("校验阿里云账号异常:{}", e.getMessage());
        }
        if (sendSmsResponse == null) {
            log.error("阿里云短信,短信发送异常,sendSmsResponse响应对象为null");
            throw new BusinessException("阿里云短信,短信发送异常,短信发送请求未收到响应");
        }
        if (sendSmsResponse.getCode() != null && ALIYUN_SMS_SUCCESS_CODE.equals(sendSmsResponse.getCode())) {
            //请求成功
            log.info("短信发送成功,{}", JSONObject.toJSONString(sendSmsResponse));
            return sendSmsResponse;
        } else {
            String message = sendSmsResponse.getMessage();
            log.error("阿里云短信:{}", message);
            throw new BusinessException("阿里云短信发送异常," + message + ", 当前模板:{" + templateCode + "}");
        }
    }
}
