package cn.bctools.message.push.dto.messagePush.wechatofficialaccount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信模板Data
 *
 **/
@Data
@Accessors(chain = true)
@ApiModel("按钮消息")
public class WechatTemplateData {

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "值")
    private String value;
    @ApiModelProperty(value = "显示颜色")
    private String color;

}
