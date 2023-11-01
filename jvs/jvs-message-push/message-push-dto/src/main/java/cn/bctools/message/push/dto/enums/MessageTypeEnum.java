package cn.bctools.message.push.dto.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    EMAIL("EMAIL","普通邮件", PlatformEnum.EMAIL),
    ALI_SMS("ALI_SMS","阿里短信", PlatformEnum.ALI_SMS),
    INSIDE_NOTIFICATION("INSIDE_NOTIFICATION","阿里短信", PlatformEnum.INSIDE_NOTIFICATION),

    // ================================企业微信-应用====================================
    WECHAT_WORK_AGENT_TEXT("WECHAT_WORK_AGENT_TEXT","文本", PlatformEnum.WECHAT_WORK_AGENT),
    WECHAT_WORK_AGENT_IMAGE("WECHAT_WORK_AGENT_IMAGE","图片", PlatformEnum.WECHAT_WORK_AGENT),
    WECHAT_WORK_AGENT_VIDEO("WECHAT_WORK_AGENT_VIDEO","视频", PlatformEnum.WECHAT_WORK_AGENT),
    WECHAT_WORK_AGENT_FILE("WECHAT_WORK_AGENT_FILE","文件", PlatformEnum.WECHAT_WORK_AGENT),
    WECHAT_WORK_AGENT_TEXTCARD("WECHAT_WORK_AGENT_TEXTCARD","文本卡片", PlatformEnum.WECHAT_WORK_AGENT),
    WECHAT_WORK_AGENT_NEWS("WECHAT_WORK_AGENT_NEWS","图文消息", PlatformEnum.WECHAT_WORK_AGENT),
    WECHAT_WORK_AGENT_MARKDOWN("WECHAT_WORK_AGENT_MARKDOWN","Markdown", PlatformEnum.WECHAT_WORK_AGENT),

    // ================================企业微信-群机器人====================================
    WECHAT_WORK_ROBOT_TEXT("WECHAT_WORK_ROBOT_TEXT","文本", PlatformEnum.WECHAT_WORK_ROBOT),
    WECHAT_WORK_ROBOT_IMAGE("WECHAT_WORK_ROBOT_IMAGE","图片", PlatformEnum.WECHAT_WORK_ROBOT),
    WECHAT_WORK_ROBOT_NEWS("WECHAT_WORK_ROBOT_NEWS","图文消息", PlatformEnum.WECHAT_WORK_ROBOT),
    WECHAT_WORK_ROBOT_MARKDOWN("WECHAT_WORK_ROBOT_MARKDOWN","Markdown", PlatformEnum.WECHAT_WORK_ROBOT),

    // ================================微信公众号====================================
    WECHAT_OFFICIAL_ACCOUNT_TEXT("WECHAT_OFFICIAL_ACCOUNT_TEXT","文本", PlatformEnum.WECHAT_OFFICIAL_ACCOUNT),
    WECHAT_OFFICIAL_ACCOUNT_NEWS("WECHAT_OFFICIAL_ACCOUNT_NEWS","图文消息", PlatformEnum.WECHAT_OFFICIAL_ACCOUNT),
    WECHAT_OFFICIAL_ACCOUNT_TEMPLATE("WECHAT_OFFICIAL_ACCOUNT_TEMPLATE","模板消息", PlatformEnum.WECHAT_OFFICIAL_ACCOUNT),

    // ================================钉钉-工作通知====================================
    DING_TALK_COPR_TEXT("DING_TALK_COPR_TEXT","文本", PlatformEnum.DING_TALK_CORP),
    DING_TALK_COPR_MARKDOWN("DING_TALK_COPR_MARKDOWN","Markdown", PlatformEnum.DING_TALK_CORP),
    DING_TALK_COPR_LINK("DING_TALK_COPR_LINK","链接消息", PlatformEnum.DING_TALK_CORP),
    DING_TALK_COPR_ACTION_CARD_SINGLE("DING_TALK_COPR_ACTION_CARD_SINGLE","卡片-单按钮", PlatformEnum.DING_TALK_CORP),
    DING_TALK_COPR_ACTION_CARD_MULTI("DING_TALK_COPR_ACTION_CARD_MULTI","卡片-多按钮", PlatformEnum.DING_TALK_CORP),
    DING_TALK_COPR_OA("DING_TALK_COPR_OA","OA消息", PlatformEnum.DING_TALK_CORP),

    // ================================钉钉-群机器人====================================
    DING_TALK_ROBOT_TEXT("DING_TALK_ROBOT_TEXT","文本", PlatformEnum.DING_TALK_ROBOT),
    DING_TALK_ROBOT_MARKDOWN("DING_TALK_ROBOT_MARKDOWN","Markdown", PlatformEnum.DING_TALK_ROBOT),
    DING_TALK_ROBOT_LINK("DING_TALK_ROBOT_LINK","链接消息", PlatformEnum.DING_TALK_ROBOT),
    DING_TALK_ROBOT_ACTION_CARD_SINGLE("DING_TALK_ROBOT_ACTION_CARD_SINGLE","卡片-单按钮", PlatformEnum.DING_TALK_ROBOT),
    DING_TALK_ROBOT_ACTION_CARD_MULTI("DING_TALK_ROBOT_ACTION_CARD_MULTI","卡片-多按钮", PlatformEnum.DING_TALK_ROBOT),
    DING_TALK_ROBOT_FEED_CARD("DING_TALK_ROBOT_FEED_CARD","FeedCard", PlatformEnum.DING_TALK_ROBOT),
    ;
    @EnumValue
    private final String value;
    private final String desc;
    private final PlatformEnum platform;
}
