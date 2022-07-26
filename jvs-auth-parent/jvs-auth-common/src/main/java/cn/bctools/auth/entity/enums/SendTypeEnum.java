package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author guojing
 */
public enum SendTypeEnum {

    /**
     * 消息发送类型
     */
    email("email", "邮箱"),
    sms("sms", "短信"),
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
