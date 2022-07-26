package cn.bctools.dingding;

import lombok.Data;

/**
 * 钉钉，链接
 *
 * @author: GuoZi
 */
@Data
public class Link {

    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容。如果太长只会部分展示
     */
    private String text;
    /**
     * 图片URL
     */
    private String picUrl;
    /**
     * 点击消息跳转的URL
     */
    private String messageUrl;

    public Link(String title, String text, String picUrl, String messageUrl) {
        this.title = title;
        this.text = text;
        if (picUrl != null) {
            this.picUrl = picUrl;
        } else {
            this.picUrl = "";
        }
        this.messageUrl = messageUrl;
    }

}
