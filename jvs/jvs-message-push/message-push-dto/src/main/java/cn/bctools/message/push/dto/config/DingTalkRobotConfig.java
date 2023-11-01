package cn.bctools.message.push.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 钉钉群机器人消息配置
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("钉钉群机器人消息配置")
public class DingTalkRobotConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = -9206902816158196669L;

    @ApiModelProperty(value = "群机器人的webhook")
    private String webhook;

}
