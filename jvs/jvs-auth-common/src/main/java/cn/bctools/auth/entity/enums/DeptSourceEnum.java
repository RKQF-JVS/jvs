package cn.bctools.auth.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 部门信息来源
 */
@Getter
@AllArgsConstructor
public enum DeptSourceEnum {
    SYS, DINGTALK_INSIDE, WECHAT_ENTERPRISE_WEB
}
