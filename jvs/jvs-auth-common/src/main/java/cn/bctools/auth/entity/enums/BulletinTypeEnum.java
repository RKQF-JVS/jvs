package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author
 */

@Getter
@AllArgsConstructor
public enum BulletinTypeEnum {

    PC("PC", "PC端"),
    MOBILE("MOBILE", "移动端"),
    ;

    @EnumValue
    @JsonValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }
}
