package cn.bctools.message.push.dto.messagePush.dingtalk.robot;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 钉钉群消息-卡片消息-独立跳转类型
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉群消息-卡片消息-独立跳转类型")
public class ActionCardMultiMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -3289428483627765265L;

    @ApiModelProperty("是否@所有人")
    private boolean isAtAll;

    @ApiModelProperty("首屏会话透出的展示内容")
    private String title;

    @ApiModelProperty("markdown格式的消息")
    private String text;

    @ApiModelProperty("按钮排列方式 0按钮竖直排列 1按钮横向排列")
    private String btnOrientation = "0";

    @ApiModelProperty("按钮")
    private List<BtnJsonDTO> btns;

}
