package cn.bctools.message.push.dto.messagePush.dingtalk.corp;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 钉钉工作消息-oa
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉工作消息-oa")
public class OaMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -4488454049089666314L;


    @ApiModelProperty("是否发送给企业全部用户，注意钉钉限制只能发3次全员消息")
    private boolean toAllUser;

    @ApiModelProperty("接收人的部门id列表，接收者的部门id列表，多个用,隔开")
    private String deptIdList;

    @ApiModelProperty("消息点击链接地址，当发送消息为小程序时支持小程序跳转链接。")
    private String messageUrl;

    @ApiModelProperty("PC端点击消息时跳转到的地址")
    private String pcMessageUrl;

    @ApiModelProperty("消息头部的背景颜色。长度限制为8个英文字符，其中前2为表示透明度，后6位表示颜色值。不要添加0x。")
    private String headBgColor;

    @ApiModelProperty("消息的头部标题，长度限制为最多10个字符。")
    private String headText;

    @ApiModelProperty("状态栏文案")
    private String statusBarStatusValue;

    @ApiModelProperty("状态栏背景色，默认为黑色，推荐0xFF加六位颜色值。")
    private String statusBarStatusBg;

    @ApiModelProperty("消息体的标题，建议50个字符以内。")
    private String bodyTitle;

    @ApiModelProperty("消息体的表单，最多显示6个，超过会被隐藏")
    private List<BodyForm> bodyForms;

    @ApiModelProperty("单行富文本信息的数目")
    private String richNum;

    @ApiModelProperty("单行富文本信息的单位")
    private String richUnit;

    @ApiModelProperty("消息体的内容，最多显示3行")
    private String content;

    @ApiModelProperty("消息体中的图片，建议宽600像素 x 400像素，宽高比3 : 2")
    private String image;

    @ApiModelProperty("自定义的附件数目。此数字仅供显示，钉钉不作验证")
    private String fileCount;

    @ApiModelProperty("自定义的作者名字")
    private String author;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BodyForm {
        @ApiModelProperty("关键字")
        private String key;
        @ApiModelProperty("关键字对应的值")
        private String value;
    }

}
