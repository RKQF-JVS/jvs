package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型
 *
 * @author 
 */
@Getter
@AllArgsConstructor
public enum DictTypeEnum {

    /**
     * 字典类型-系统内置（不可修改）
     */
    SYSTEM("SYSTEM", "系统内置"),

    /**
     * 字典类型-业务类型
     */
    BIZ("BIZ", "业务类"),
    ;

    @EnumValue
    @JsonEnumDefaultValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }

}
