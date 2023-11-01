package cn.bctools.message.push.websocket;

import cn.bctools.message.push.entity.InsideNotice;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class RedisSubscriber {

    private final InsideNoticeEndPoint insideNoticeEndPoint;

    public void onMessage(String message, String pattern) {
        try {
            log.info("订阅消息："+message);
            Object parse = JSONObject.parse(message);
            String s = parse.toString();
            InsideNotice insideNotice = JSONObject.parseObject(s , InsideNotice.class);
            insideNoticeEndPoint.sendMessageTo(insideNotice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
