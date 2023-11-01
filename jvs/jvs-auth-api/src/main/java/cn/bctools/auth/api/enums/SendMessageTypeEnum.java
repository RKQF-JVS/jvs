package cn.bctools.auth.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发送的消息类型，只针对系统的站内信消息
 *
 * @author 
 */
@Getter
@AllArgsConstructor
public enum SendMessageTypeEnum {

    /**
     * 广播消息
     */
    broadcast("broadcast", "广播消息"),
    /**
     * 警告消息
     */
    warning("warning", "警告消息"),
    /**
     * 通知消息
     */
    notification("notification", "通知消息"),
    /**
     * 系统消息
     */
    system("system", "系统消息"),
    /**
     * 业务消息
     */
    business("business", "业务消息");

    private final String code;

    private final String msg;

    public static SendMessageTypeEnum getCode(String code) {
        for (SendMessageTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
