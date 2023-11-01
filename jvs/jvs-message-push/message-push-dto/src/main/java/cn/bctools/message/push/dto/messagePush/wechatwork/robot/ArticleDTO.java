package cn.bctools.message.push.dto.messagePush.wechatwork.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 图文消息
 *
 * @author 钟宝林
 * @since 2021/4/8/008 12:15
 **/
@Data
@Accessors(chain = true)
@ApiModel("图文消息")
public class ArticleDTO {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述 超过512个字节，超过会自动截断")
    private String description;

    @ApiModelProperty(value = "点击跳转链接")
    private String url;

    @ApiModelProperty(value = "图片链接 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图1068*455，小图150*150。")
    private String picurl;

}
