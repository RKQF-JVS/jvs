package cn.bctools.message.push.dto.messagePush.dingtalk.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 按钮
 *
 **/
@Data
@Accessors(chain = true)
@ApiModel("按钮消息")
public class BtnJsonDTO {

    @ApiModelProperty("按钮标题")
    private String title;
    @ApiModelProperty("点击按钮触发的URL")
    private String actionURL;

}
