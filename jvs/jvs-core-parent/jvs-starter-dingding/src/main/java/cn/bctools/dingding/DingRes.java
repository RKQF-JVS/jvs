package cn.bctools.dingding;

import lombok.Data;

/**
 * 钉钉，消息发送结果
 *
 * @author: GuoZi
 */
@Data
public class DingRes {

    /**
     * 错误码
     */
    private Long errcode;
    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 判断是否请求成功
     *
     * @return 判断结果
     */
    public boolean is() {
        return errcode == 0;
    }

}
