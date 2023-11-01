package cn.bctools.message.push.dto.messagePush.dingtalk.robot;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 钉钉群消息link类型
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉群消息link类型")
public class LinkMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -3289428483627765265L;


    @ApiModelProperty("是否@所有人")
    private boolean isAtAll;

    @ApiModelProperty("消息标题")
    private String title;

    @ApiModelProperty("消息内容。如果太长只会部分展示。")
    private String text;

    @ApiModelProperty("点击消息跳转的URL")
    private String messageUrl;

    @ApiModelProperty("图片URL")
    private String picUrl;

}
