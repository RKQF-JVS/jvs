package cn.bctools.message.push.dto.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessagePushStatusEnum {
    WAIT("WAIT","未开始"),
    SUCCESS("SUCCESS","成功"),
    FAILED("FAILED","失败");

    @EnumValue
    private final String value;
    private final String desc;
}
