package cn.bctools.gateway.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源标识声明，为按钮还是前端说明
 * 按钮为权限控制，说明为前端描述，可进行后端替换描述值
 *
 * @author Administrator
 */
@Getter
@AllArgsConstructor
public enum PermissionTypeEnum {

    /**
     * 按钮
     */
    button("button", "按钮"),
    /**
     * 说明
     */
    remark("remark", "说明");

    @EnumValue
    @JsonEnumDefaultValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }

}
