package cn.bctools.auth.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <用户类型>
 *
 * @author auto
 **/
@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    /**
     * 后端用户
     */
    BACKEND_USER,
    /**
     * 前端用户
     */
    FRONT_USER,
    /**
     * 其他第三方用户
     */
    OTHER_USER,
    ;
}
