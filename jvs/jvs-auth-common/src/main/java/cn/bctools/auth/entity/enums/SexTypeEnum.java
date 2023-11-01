package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户性别类型
 *
 * @author Administrator
 */
@Getter
@AllArgsConstructor
public enum SexTypeEnum {

    /**
     * 保密
     */
    unknown("unknown", "保密"),
    /**
     * 男
     */
    male("male", "男"),
    /**
     * 女
     */
    female("female", "女");

    @EnumValue
    @JsonEnumDefaultValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }

    public static SexTypeEnum getByDesc(String desc) {
        for (SexTypeEnum value : values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return unknown;
    }
}
