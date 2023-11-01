package cn.bctools.message.push.dto.messagePush.dingtalk.corp;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 钉钉工作通知发送DTO
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("钉钉工作通知发送DTO")
public class TextMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -3289428483627765265L;

    @ApiModelProperty("是否发送给企业全部用户，注意钉钉限制只能发3次全员消息")
    private boolean toAllUser;


    @ApiModelProperty("接收人的部门id列表，接收者的部门id列表，多个用,隔开")
    private String deptIdList;

    @ApiModelProperty("请输入内容...")
    private String content;

}
