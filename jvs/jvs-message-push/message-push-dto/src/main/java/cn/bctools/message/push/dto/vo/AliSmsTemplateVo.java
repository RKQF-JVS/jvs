package cn.bctools.message.push.dto.vo;

import lombok.Data;

@Data
public class AliSmsTemplateVo {

    /**
     * 模板审批状态
     */
    public String auditStatus;

    /**
     * 短信模板的创建时间，格式为yyyy-MM-dd HH:mm:ss。
     */
    public String createDate;

    /**
     * 工单ID
     */
    public String orderId;

    /**
     * 审核备注
     * <p>
     * 如果审核状态为审核通过或审核中，参数Reason显示为“无审核备注”
     * 如果审核状态为审核未通过，参数Reason显示审核的具体原因
     */
    public Object reason;

    /**
     * 短信模板CODE
     */
    public String templateCode;

    /**
     * 模板内容
     */
    public String templateContent;

    /**
     * 模板名称
     */
    public String templateName;

    /**
     * 模板类型。取值：
     * 0：短信通知
     * 1：推广短信
     * 2：验证码短信
     * 6：国际/港澳台短信
     * 7：数字短信
     */
    public Integer templateType;
    /**
     * 是否是登录验证码
     */
    public Boolean loginCode;
}
