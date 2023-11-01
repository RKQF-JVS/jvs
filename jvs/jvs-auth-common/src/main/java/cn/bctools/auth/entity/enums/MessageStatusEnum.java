package cn.bctools.auth.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态
 *
 * @author: GuoZi
 */
@Getter
@AllArgsConstructor
public enum MessageStatusEnum {

    /**
     * 发送失败
     */
    SEND_FAIL(0, "失败"),
    /**
     * 发送成功
     */
    SEND_SUCCESS(1, "成功"),
    /**
     * 未发送
     */
    HAS_NOT_SEND(2, "未发送"),
    /**
     * 发送中
     */
    SENDING(3, "发送中"),
    ;

    private final Integer code;
    private final String name;

    public static String getNameByCode(Integer code) {
        for (MessageStatusEnum value : MessageStatusEnum.values()) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return null;
    }

}
