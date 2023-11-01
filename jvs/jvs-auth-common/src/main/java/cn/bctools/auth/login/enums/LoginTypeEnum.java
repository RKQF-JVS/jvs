package cn.bctools.auth.login.enums;

import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 登录授权类型
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    /**
     * value：配置类型值
     * desc：描述
     * configType：登录授权配置类型
     */
    DINGTALK_INSIDE("DINGTALK_INSIDE", "钉钉内部应用H5免登", SysConfigTypeEnum.DING_H5),
    DINGTALK_SCAN("DINGTALK_SCAN", "钉钉扫码登录", SysConfigTypeEnum.DING_H5),
    WECHAT_ENTERPRISE_WEB("WECHAT_ENTERPRISE_WEB", "企业微信", SysConfigTypeEnum.WX_ENTERPRISE),
    WECHAT_ENTERPRISE("WECHAT_ENTERPRISE", "企业微信扫码登录", SysConfigTypeEnum.WX_ENTERPRISE),
    WECHAT_MP("WECHAT_MP", "微信公众号", SysConfigTypeEnum.WECHAT_MP),
    LDAP("LDAP", "LDAP", SysConfigTypeEnum.LDAP),
    ;

    private final String value;
    private final String desc;
    private final SysConfigTypeEnum configType;

    public static LoginTypeEnum getType(String value) {
        for (LoginTypeEnum typeEnum : LoginTypeEnum.values()) {
            if (value.equals(typeEnum.getValue())) {
                return typeEnum;
            }
        }
        return null;
    }
}
