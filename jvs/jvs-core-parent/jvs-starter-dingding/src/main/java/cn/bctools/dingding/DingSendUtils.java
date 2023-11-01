package cn.bctools.dingding;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.common.utils.ObjectNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 钉钉消息发送组件
 *
 * @author: GuoZi
 */
@Slf4j
@Component
@AllArgsConstructor
public class DingSendUtils {

    DingDingConfig dingDingConfig;

    /**
     * 发送普通文本消息
     *
     * @param content 消息内容
     * @return 发送结果
     */
    public DingRes sendMessage(String content) {
        return sendMessage(content, dingDingConfig.getPhones());
    }

    public DingRes sendMessage(String content, List<String> mobiles) {
        return sendMessage(null, null, content, mobiles);
    }

    public DingRes sendMessage(String webhook, String secret, String content, List<String> mobiles) {
        webhook = getUrl(webhook, secret);
        TestMessage testMessage = new TestMessage(content, mobiles);
        String body = HttpUtil.post(webhook, JSONObject.toJSONString(testMessage));
        DingRes dingRes = JSONObject.parseObject(body, DingRes.class);
        log.debug("钉钉发送结果:{}", JSONObject.toJSONString(dingRes));
        return dingRes;
    }


    /**
     * 发送link类型消息
     *
     * @param webhook    钉钉群组的机器人的Hook地址
     * @param title      消息标题
     * @param text       消息内容。如果太长只会部分展示
     * @param picUrl     图片URL
     * @param messageUrl 点击消息跳转的URL
     * @return 发送结果
     */
    public boolean sendLinkMessage(String title, String text, String picUrl, String messageUrl) {
        return sendLinkMessage(null, null, title, text, picUrl, messageUrl);
    }

    public boolean sendLinkMessage(String webhook, String secret, String title, String text, String picUrl, String messageUrl) {
        webhook = getUrl(webhook, secret);
        LinkMessage message = new LinkMessage(title, text, picUrl, messageUrl);
        String body = HttpUtil.post(webhook, JSONObject.toJSONString(message));
        DingRes dingRes = JSONObject.parseObject(body, DingRes.class);
        return dingRes.is();
    }


    public String getUrl(String url, String secret) {
        if (ObjectNull.isNull(url, secret)) {
            url = dingDingConfig.getUrl();
            secret = dingDingConfig.getUrl();
        }
        // 使用加签方式传输
        Long timestamp = System.currentTimeMillis();
        String sign = null;
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode((Base64.encode(signData)), "UTF-8");
        } catch (Exception e) {
            log.error("加密出错", e);
        }
        url = url + "&timestamp=" + timestamp + "&sign=" + sign;
        return url;
    }

}
