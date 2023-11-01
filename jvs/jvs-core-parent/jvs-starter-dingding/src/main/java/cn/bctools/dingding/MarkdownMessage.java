package cn.bctools.dingding;

import lombok.Data;

import java.util.List;

/**
 * 钉钉，markdown消息
 *
 * @author: GuoZi
 */
@Data
public class MarkdownMessage {

    /**
     * 消息类型
     */
    private String msgtype = "markdown";
    /**
     * markdown信息
     */
    private Markdown markdown;
    /**
     * 通知@信息
     */
    private At at;

    public MarkdownMessage(String title, String text, List<String> atMobiles) {
        this.markdown = new Markdown(title, text);
        this.at = new At(atMobiles);
    }

}
