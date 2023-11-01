package cn.bctools.sms.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统预置的短信模板
 *
 * @author : auto
 */
@Getter
@AllArgsConstructor
public enum SmsTemplate {

    /**
     * 通用验证码
     */
    CODE("通用验证码", "您好,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 登录
     */
    LOGIN("登录", "您正在登陆操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 注册,暂未使用到
     */
    REGISTER("注册", "您正在注册操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 租户开通成功的通知
     */
    TENANT_REGISTER_SUCCESS("租户开通成功", "尊敬的${userName}，您的账户已开通成功，感谢您的使用,如您在使用过程中有任何的疑问,欢迎咨询平台方。"),

    /**
     * 用户注册成功的通知
     */
    USER_REGISTER_SUCCESS("注册成功", "尊敬的${userName}，您的账户已注册成功，初始密码为${password}，请尽快登录修改您的密码。"),

    /**
     * 绑定手机
     */
    BINDING("绑定手机号", "您正在进行绑定手机号操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 忘记密码
     */
    FORGOT("忘记密码", "您正在进行忘记密码操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 重置密码
     */
    RESET("重置密码", "您正在进行重置密码操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 修改密码
     */
    CHANGE("修改密码", "您正在进行修改密码操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。"),

    /**
     * 手机号重置
     */
    CHANGE_MOBILE_PHONE("手机号重置", "您正在进行手机号重置操作,验证码为${code},有效时间${expireMinutes}分钟,泄漏有风险。");

    /**
     * 阿里的短信模板约定
     * 在阿里控制台配置完短信签名,以及短信模板之后.
     * 注意,使用阿里的模板之后,传递的map视模板类型而有所限制,比如,验证码场景中,map要求,参数只能为数字和字母,不能有中文字符
     * 模板名称  content 必须为阿里短信模板的temlateCode ,签名存在 title中
     * ALI_SMS_APPOINTMENT("手机号重置","SMS_187752507");
     */
    /**
     * 模板名称
     */
    String templateName;

    /**
     * 内容
     */
    String content;

}
