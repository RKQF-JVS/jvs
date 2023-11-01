package cn.bctools.gateway.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author guojing
 */
public class JvsMessageListenerAdapter extends MessageListenerAdapter {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        onMessage(JSONObject.parse(new String(message.getBody())).toString());
    }

    public void onMessage(String s) {

    }
}
