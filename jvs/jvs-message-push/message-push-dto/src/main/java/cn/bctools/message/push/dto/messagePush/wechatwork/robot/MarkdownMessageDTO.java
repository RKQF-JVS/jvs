package cn.bctools.message.push.dto.messagePush.wechatwork.robot;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 企业微信Markdown
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("企业微信Markdown")
public class MarkdownMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 8123659270032033936L;

    @ApiModelProperty("请输入Markdown内容...")
    private String content;


}
