package cn.bctools.dingding;

import lombok.Data;

import java.util.List;

/**
 * 钉钉，普通文本信息
 *
 * @author: GuoZi
 */
@Data
public class TestMessage {

    /**
     * 消息类型
     */
    private String msgtype = "text";
    /**
     * 文本内容
     */
    private Text text;
    /**
     * 通知@信息
     */
    private At at;

    public TestMessage(String content, List<String> atMobiles) {
        this.text = new Text(content);
        this.at = new At(atMobiles);
    }

}
