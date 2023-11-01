package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型，为租户角色，和用户角色，
 * 租户角色，根据组织获取赋予菜单或数据权限功能
 * 用户角色，根据将菜单和数据权限赋予给某些用户
 *
 * @author Administrator
 */
@Getter
@AllArgsConstructor
public enum RoleTypeEnum {

    /**
     * 租户角色
     */
    tenantRole("tenantRole", "租户角色"),
    /**
     * 用户角色
     */
    userRole("userRole", "用户角色");

    @EnumValue
    @JsonEnumDefaultValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }

}
