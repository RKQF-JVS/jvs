package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限资源表
 * 资源标识属性为，有菜单，租户标识 ，按钮标识
 * @author
 */
@Getter
@AllArgsConstructor
public enum RolePermissionEnum {

    /**
     * 菜单
     */
    menu("menu", "菜单"),
    /**
     * 租户
     */
    tenant("tenant", "租户"),
    /**
     * 按钮
     */
    button("button", "按钮");

    @EnumValue
    @JsonEnumDefaultValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }

}
