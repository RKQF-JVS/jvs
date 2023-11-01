package cn.bctools.auth.entity.enums;

import cn.bctools.common.enums.ConfigTypeEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 配置类型
 */

@Getter
@AllArgsConstructor
public enum SysConfigTypeEnum {

    /**
     * value：配置类型值
     * desc：描述
     * authType：登录类型
     */
    BASIC(ConfigTypeEnum.BASIC.name(), "基本配置", null),
    DING_H5(ConfigTypeEnum.DING_H5.name(), "钉钉H5微应用", OAuthTypeEnum.dd),
    WX_ENTERPRISE(ConfigTypeEnum.WX_ENTERPRISE.name(), "企业微信", OAuthTypeEnum.wxenterprise),
    WECHAT_MP(ConfigTypeEnum.WECHAT_MP.name(), "微信公众号", OAuthTypeEnum.wxmp),
    LDAP(ConfigTypeEnum.LDAP.name(), "ldap登录", OAuthTypeEnum.ldap),
    SMS(ConfigTypeEnum.SMS.name(), "短信配置", null),
    APPBASCSETTING(ConfigTypeEnum.APPBASCSETTING.name(), "移动端基础配置", null),
    WECHAT_MP_MESSAGE(ConfigTypeEnum.WECHAT_MP_MESSAGE.name(), "微信公众号消息", null),
    OAUTHLOGIN(ConfigTypeEnum.OAUTHLOGIN.name(), "登录授权配置", null),
    EMAIL(ConfigTypeEnum.EMAIL.name(), "邮件配置", null);

    @EnumValue
    @JsonValue
    public final String value;
    public final String desc;
    public final OAuthTypeEnum authType;

    public static SysConfigTypeEnum getType(OAuthTypeEnum oAuthType) {
        for (SysConfigTypeEnum value : SysConfigTypeEnum.values()) {
            if (oAuthType.equals(value.getAuthType())) {
                return value;
            }
        }
        return null;
    }

    public static SysConfigTypeEnum getConfigType(String value) {
        for (SysConfigTypeEnum typeEnum : SysConfigTypeEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
