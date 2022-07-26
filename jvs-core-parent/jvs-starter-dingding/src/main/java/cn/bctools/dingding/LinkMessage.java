package cn.bctools.dingding;

import lombok.Data;

/**
 * 钉钉消息：携带链接
 *
 * @author: GuoZi
 */
@Data
public class LinkMessage {

    /**
     * 消息类型
     */
    private String msgtype = "link";
    /**
     * 连接信息
     */
    private Link link;

    public LinkMessage(String title, String text, String picUrl, String messageUrl) {
        this.link = new Link(title, text, picUrl, messageUrl);
    }

}
