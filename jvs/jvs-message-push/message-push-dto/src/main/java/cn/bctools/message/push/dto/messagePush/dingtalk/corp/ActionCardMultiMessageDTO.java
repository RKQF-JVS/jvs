package cn.bctools.message.push.dto.messagePush.dingtalk.corp;

import cn.bctools.message.push.dto.messagePush.BaseMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 钉钉卡片消息-独立跳转
 *
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain =true)
@ApiModel("钉钉卡片消息-独立跳转")
public class ActionCardMultiMessageDTO extends BaseMessage implements Serializable {
    private static final long serialVersionUID = -3289428483627765265L;

    @ApiModelProperty("是否发送给企业全部用户，注意钉钉限制只能发3次全员消息")
    private boolean toAllUser;

    @ApiModelProperty("接收人的部门id列表，接收者的部门id列表，多个用,隔开")
    private String deptIdList;

    @ApiModelProperty("标题，最长20个字符。")
    private String title;

    @ApiModelProperty("跳转链接")
    private String actionUrl;

    @ApiModelProperty( value = "按钮排列方式 0竖直排列 1横向排列")
    private String btnOrientation = "0";

    @ApiModelProperty( "消息内容，支持markdown，语法参考标准markdown语法。建议1000个字符以内。")
    private String markdown;

    @ApiModelProperty("按钮设置")
    private List<BtnJsonDTO> btnJsonList;
}
