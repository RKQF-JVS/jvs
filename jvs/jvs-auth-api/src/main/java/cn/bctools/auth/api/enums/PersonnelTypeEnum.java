package cn.bctools.auth.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 通用的人员选择类型
 */
@Getter
@AllArgsConstructor
public enum PersonnelTypeEnum {

    /**
     * 用户
     */
    user("user"),
    /**
     * 部门
     */
    dept("dept"),
    /**
     * 角色
     */
    role("role"),
    /**
     * 用户组
     */
    group("group"),
    /**自定义*/
    custom("custom"),
    /**所有*/
    all("all"),
    ;

    @JsonValue
    private final String value;
}
