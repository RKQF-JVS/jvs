package cn.bctools.message.push.dto.messagePush.dingtalk.robot;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 钉钉群消息markdown类型DTO
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉群消息markdown类型DTO")
public class MarkdownMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -3289428483627765265L;

    @ApiModelProperty("是否@所有人")
    private boolean isAtAll;

    @ApiModelProperty("首屏会话透出的展示内容")
    private String title;

    @ApiModelProperty("markdown格式的消息")
    private String text;

}
