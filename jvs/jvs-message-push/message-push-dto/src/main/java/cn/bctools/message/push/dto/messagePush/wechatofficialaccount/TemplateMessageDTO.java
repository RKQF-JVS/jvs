package cn.bctools.message.push.dto.messagePush.wechatofficialaccount;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 微信公众号模板消息
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("微信公众号模板消息")
public class TemplateMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 2978534939532888543L;

    @ApiModelProperty("公众号模板id")
    private String wechatTemplateId;

    @ApiModelProperty("点击跳转链接")
    private String url;

    @ApiModelProperty("小程序appId")
    private String miniAppId;

    @ApiModelProperty("小程序页面路径")
    private String miniPagePath;

    @ApiModelProperty("模板变量")
    private List<WechatTemplateData> templateDataList;

}
