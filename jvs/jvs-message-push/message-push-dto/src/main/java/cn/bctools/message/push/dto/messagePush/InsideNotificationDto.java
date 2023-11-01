package cn.bctools.message.push.dto.messagePush;

import cn.bctools.message.push.dto.enums.InsideNotificationTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("站内推送数据")
public class InsideNotificationDto extends BaseMessage {
    private static final long serialVersionUID = 2584119180794722634L;

    @ApiModelProperty("消息批次号")
    private String requestNo;

    @ApiModelProperty("消息内容")
    private String content;

    /**
     * 值为 通知 项目
     * 不传的话默认为 通知类型
     */
    @ApiModelProperty("消息通知大类")
    private InsideNotificationTypeEnum largeCategories;

    @ApiModelProperty("消息子类")
    private String subClass = "通知消息";

    @ApiModelProperty("消息回调地址")
    private String callBackUrl;
}
