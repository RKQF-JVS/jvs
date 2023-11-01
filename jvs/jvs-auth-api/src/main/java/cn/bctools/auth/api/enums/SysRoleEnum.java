package cn.bctools.auth.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统默认角色
 *
 * @Author: GuoZi
 */
@Getter
@AllArgsConstructor
public enum SysRoleEnum {

    /**
     * 应用管理员
     */
    APP_ADMIN("应用管理员", "应用管理员角色, 此角色信息不能删除"),
    /**
     * 系统管理员
     */
    SYS_ADMIN("系统管理员", "系统管理员角色, 此角色信息不能删除");

    /**
     * 角色名称
     */
    private final String name;
    /**
     * 角色描述
     */
    private final String desc;

    /**
     * 是否包含系统角色名称
     *
     * @return 判断结果
     */
    public static boolean containName(String roleName) {
        for (SysRoleEnum value : SysRoleEnum.values()) {
            if (value.name.equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取系统角色名称集合
     *
     * @return 名称集合
     */
    public static List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (SysRoleEnum value : SysRoleEnum.values()) {
            names.add(value.name);
        }
        return names;
    }

}
