package cn.bctools.message.push.dto.messagePush.dingtalk.corp;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 钉钉链接消息
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉链接消息")
public class LinkMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 6529460286674167742L;

    @ApiModelProperty("是否发送给企业全部用户，注意钉钉限制只能发3次全员消息")
    private boolean toAllUser;

    @ApiModelProperty("接收人的部门id列表，接收者的部门id列表，多个用,隔开")
    private String deptIdList;

    @ApiModelProperty("消息点击链接地址，当发送消息为小程序时支持小程序跳转链接。")
    private String messageUrl;

    @ApiModelProperty("图片地址，可以通过上传媒体文件接口获取。")
    private String picUrl;

    @ApiModelProperty("消息标题，建议100字符以内。")
    private String title;

    @ApiModelProperty("消息描述，建议500字符以内。")
    private String text;

}
