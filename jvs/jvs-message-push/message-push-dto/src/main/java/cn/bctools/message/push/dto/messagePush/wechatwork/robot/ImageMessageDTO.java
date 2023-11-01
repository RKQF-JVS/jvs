package cn.bctools.message.push.dto.messagePush.wechatwork.robot;

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
public class ImageMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 7412950115675650317L;
    
    @ApiModelProperty("图片内容的base64编码")
    private String base64;

    @ApiModelProperty("图片内容（base64编码前）的md5值")
    private String md5;

}
