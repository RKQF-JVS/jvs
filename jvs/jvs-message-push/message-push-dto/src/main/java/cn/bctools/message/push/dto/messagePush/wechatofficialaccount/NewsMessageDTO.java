package cn.bctools.message.push.dto.messagePush.wechatofficialaccount;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信公众号图文消息发送
 *
 * @author 钟宝林
 * @since 2021/4/7/007 17:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("微信公众号图文消息发送")
public class NewsMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 7034106110120563906L;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("描述，超过512个字节，超过会自动截断")
    private String description;

    @ApiModelProperty("点击跳转链接")
    private String url;

    @ApiModelProperty("图片链接，图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图1068*455，小图150*150。")
    private String picUrl;

}
