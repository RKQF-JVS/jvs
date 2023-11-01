package cn.bctools.message.push.dto.messagePush;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("阿里短信 dto")
public class AliSmsDto extends BaseMessage{
    private static final long serialVersionUID = -8597570707025840215L;

    /**
     * 您可以登录短信服务控制台 https://dysms.console.aliyun.com/overview，
     * 选择国内消息或国际/港澳台消息，在签名管理页面获取。
     */
    @ApiModelProperty("短信签名名称")
    private String signName;

    /**
     * 您可以登录短信服务控制台 https://dysms.console.aliyun.com/overview，
     * 选择国内消息或国际/港澳台消息，在模板管理页面查看模板CODE。
     */
    @ApiModelProperty("短信模板CODE")
    private String templateCode;

    /**
     * JSON格式。支持传入多个参数，示例：{"name":"张三","number":"15038****76"}
     */
    @ApiModelProperty("短信模板变量对应的实际值")
    private String templateParam;

    /**
     * 上行短信指发送给通信服务提供商的短信，
     * 用于定制某种服务、完成查询，或是办理某种业务等，
     * 需要收费，按运营商普通短信资费进行扣费
     */
    @ApiModelProperty("上行短信扩展码")
    private String smsUpExtendCode;

    @ApiModelProperty("外部流水扩展字段")
    private String outId;

}
