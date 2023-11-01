package cn.bctools.message.push.dto.messagePush.wechatwork.agent;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 图片、文件消息
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("图片、文件消息")
public class MediaMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 7412950115675650317L;

    @ApiModelProperty("PartyID列表，非必填，多个接受者用‘|’分隔。当touser为@all时忽略本参数")
    private String toParty;

    @ApiModelProperty("TagID列表，非必填，多个接受者用‘|’分隔。当touser为@all时忽略本参数")
    private String toTag;

    @ApiModelProperty("素材id")
    private String mediaId;

}
