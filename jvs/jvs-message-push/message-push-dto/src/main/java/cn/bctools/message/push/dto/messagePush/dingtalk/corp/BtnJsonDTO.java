package cn.bctools.message.push.dto.messagePush.dingtalk.corp;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 钉钉按钮跳转链接
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉按钮跳转链接")
public class BtnJsonDTO extends BaseMessage implements Serializable {

    @ApiModelProperty("按钮标题")
    private String title;
    @ApiModelProperty("跳转链接")
    private String actionUrl;

}
