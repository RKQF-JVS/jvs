package cn.bctools.message.push.dto.messagePush.wechatwork.agent;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 卡片信息
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("卡片信息")
public class TextCardMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -5830938694539681793L;

    @ApiModelProperty("PartyID列表，非必填，多个接受者用‘|’分隔。当touser为@all时忽略本参数")
    private String toParty;

    @ApiModelProperty("TagID列表，非必填，多个接受者用‘|’分隔。当touser为@all时忽略本参数")
    private String toTag;

    @ApiModelProperty("点击后跳转的链接。最长2048字节，请确保包含了协议头(http/https)")
    private String url;

    @ApiModelProperty("标题，不超过128个字节，超过会自动截断")
    private String title;

    @ApiModelProperty("描述，不超过512个字节，超过会自动截断")
    private String description;

    @ApiModelProperty("按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。")
    private String btntxt;
}
