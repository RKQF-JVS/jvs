package cn.bctools.message.push.dto.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InsideNotificationTypeEnum {

    notice("notice","通知"),
    project("project","项目");

    @EnumValue
    private final String code;
    private final String desc;

}
