package cn.bctools.message.push.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 钉钉工作通知配置
 *
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("钉钉工作通知配置")
public class DingTalkCorpConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = -9206902816158196669L;

    @ApiModelProperty(value = "应用appKey")
    private String appKey;
    @ApiModelProperty(value = "应用Secret")
    private String AppSecret;
    @ApiModelProperty(value = "应用agentId")
    private Integer agentId;

}
