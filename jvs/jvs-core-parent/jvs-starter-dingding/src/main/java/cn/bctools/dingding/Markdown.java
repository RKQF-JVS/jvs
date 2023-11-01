package cn.bctools.dingding;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 钉钉，markdown
 *
 * @author: GuoZi
 */
@Data
@AllArgsConstructor
public class Markdown {

    /**
     * 首屏会话透出的展示内容
     */
    private String title;
    /**
     * markdown格式的消息
     */
    private String text;

}
