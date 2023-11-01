package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 固定的配置字段
 */
@Getter
@AllArgsConstructor
public enum SysConfigFieldEnum {
    /**
     * value：字段名
     * desc：描述
     * secrecy：TRUE-保密字段，FALSE-普通字段
     */
    TENANT_ID("jvsTenantId", "租户id", Boolean.FALSE),
    ENABLE("enable", "TRUE-启用配置,FALSE-不启用配置", Boolean.FALSE),
    ENABLE_SCAN("enableScan", "TRUE-启用扫码登录,FALSE-不启扫码登录", Boolean.FALSE),
    DOMAIN("domain", "域名", Boolean.FALSE),
    APP_ID("appId", "三方平台登录授权AppKey", Boolean.FALSE),
    APP_SECRET("appSecret", "三方平台登录授权AppSecret", Boolean.TRUE),
    AGENT_ID("agentId", "三方平台应用AgentId", Boolean.FALSE),
    CORP_ID("corpId", "企业id", Boolean.FALSE),
    REDIRECT_URI("redirectUri", "登录授权重定向地址", Boolean.FALSE),
    AES_KEY("aesKey", "微信公众号的EncodingAESKey", Boolean.TRUE),
    TOKEN("token", "微信公众号的token", Boolean.TRUE),
    BASE("base", "LDAP base", Boolean.FALSE),
    URLS("urls", "服务地址，多个以分号分隔", Boolean.FALSE),
    USERNAME("username", "用户名", Boolean.FALSE),
    PASSWORD("password", "密码", Boolean.TRUE),
    ACCOUNT_ATTRIBUTE("accountAttribute", "LDAP账号字段", Boolean.FALSE),

    ;

    @EnumValue
    @JsonValue
    public final String value;
    public final String desc;
    public final Boolean secrecy;

    public static SysConfigFieldEnum getField(String fieldName) {
        for (SysConfigFieldEnum value : SysConfigFieldEnum.values()) {
            if (value.getValue().equals(fieldName)) {
                return value;
            }
        }
        return null;
    }
}
