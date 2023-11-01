package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author 
 */
public enum SendTypeEnum {

    /**
     * 消息发送类型
     */
    email("email", "邮箱"),
    sms("sms", "短信"),
    DING_H5("DING_H5", "钉钉_H5"),
    WECHAT_MP_MESSAGE("WECHAT_MP_MESSAGE", "微信公众号"),
    WX_ENTERPRISE("WX_ENTERPRISE", "企业微信"),
    interior("interior", "站内信");

    @EnumValue
    private String code;

    String msg;

    SendTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SendTypeEnum getCode(String code) {
        for (SendTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
