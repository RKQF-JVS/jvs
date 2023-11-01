package cn.bctools.auth.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 授权来源
 */
@Getter
@AllArgsConstructor
public enum AuthSourceEnum {
    /**
     * SYS：低代码系统
     * DINGTALK_INSIDE：钉钉
     * WECHAT_ENTERPRISE_WEB：企业微信
     */
    SYS, DINGTALK_INSIDE, WECHAT_ENTERPRISE_WEB
}
