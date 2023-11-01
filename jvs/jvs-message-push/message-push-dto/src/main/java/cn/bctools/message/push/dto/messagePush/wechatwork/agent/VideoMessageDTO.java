package cn.bctools.message.push.dto.messagePush.wechatwork.agent;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 视频消息类型
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("视频消息类型")
public class VideoMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -5830938694539681793L;
    
    @ApiModelProperty("PartyID列表，非必填，多个接受者用‘|’分隔。当touser为@all时忽略本参数")
    private String toParty;

    @ApiModelProperty("TagID列表，非必填，多个接受者用‘|’分隔。当touser为@all时忽略本参数")
    private String toTag;

    @ApiModelProperty("视频素材id")
    private String mediaId;

    @ApiModelProperty("视频消息的标题，不超过128个字节，超过会自动截断")
    private String title;

    @ApiModelProperty("视频消息的描述，不超过512个字节，超过会自动截断")
    private String description;
}
