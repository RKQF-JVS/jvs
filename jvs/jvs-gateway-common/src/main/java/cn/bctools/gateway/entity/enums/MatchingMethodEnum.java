package cn.bctools.gateway.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author
 */

@Getter
@AllArgsConstructor
public enum MatchingMethodEnum {

    PreMatch("PreMatch", "前匹配"),
    PostMatch("PostMatch", "后匹配"),
    PerfectMatch("PerfectMatch", "完全匹配");

    @EnumValue
    @JsonEnumDefaultValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }
}
