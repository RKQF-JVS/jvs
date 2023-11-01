package cn.bctools.message.push.dto.enums;

import cn.bctools.message.push.dto.config.*;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 平台
 */
@Getter
@AllArgsConstructor
public enum PlatformEnum {

    EMAIL("EMAIL", "邮箱", "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$", EmailConfig.class),
    WECHAT_WORK_AGENT("WECHAT_WORK_AGENT", "企业微信-应用消息",  "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$", WechatWorkAgentConfig.class),
    WECHAT_WORK_ROBOT("WECHAT_WORK_ROBOT", "企业微信-群机器人",  "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$", WechatWorkRobotConfig.class),
    WECHAT_OFFICIAL_ACCOUNT("WECHAT_OFFICIAL_ACCOUNT", "微信公众号",  "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$", WechatOfficialAccountConfig.class),
    DING_TALK_CORP("DING_TALK_CORP", "钉钉-工作通知",  "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$", DingTalkCorpConfig.class),
    DING_TALK_ROBOT("DING_TALK_ROBOT", "钉钉-群机器人",  "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$", DingTalkRobotConfig.class),
    ALI_SMS("ALI_SMS", "短信",  "", AliSmsConfig.class),
    INSIDE_NOTIFICATION("INSIDE_NOTIFICATION", "站内推送",  "", null);

    @EnumValue
    private final String code;
    private final String desc;
    private final String validateReg;
    private final Class<? extends BaseConfig> configType;
}
