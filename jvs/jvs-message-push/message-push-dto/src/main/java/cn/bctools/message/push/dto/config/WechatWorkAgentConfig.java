package cn.bctools.message.push.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 企业微信配置
 *
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel("企业微信配置")
@Accessors(chain = true)
public class WechatWorkAgentConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = -9206902816158196669L;

    @ApiModelProperty(value = "企业ID",  notes = "在此页面查看：https://work.weixin.qq.com/wework_admin/frame#profile")
    private String corpId;
    @ApiModelProperty(value = "应用Secret")
    private String secret;
    @ApiModelProperty(value = "应用agentId")
    private Integer agentId;

}
