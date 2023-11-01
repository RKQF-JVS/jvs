package cn.bctools.message.push.dto.messagePush;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel("接收人dto")
public class ReceiversDto implements Serializable {
    private static final long serialVersionUID = 7948227661021241730L;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户真实名称")
    private String userName;

    @ApiModelProperty("用户接收配置 邮件:邮件地址 微信公众号:openId 短信:手机号码 ...")
    private String receiverConfig;

    @ApiModelProperty("用户租户 如果是定时任务或其他不能从系统中获取的情况需要传")
    private String TenantId;
}
