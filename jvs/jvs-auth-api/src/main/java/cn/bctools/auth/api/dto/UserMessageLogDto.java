package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: ZhuXiaoKang
 * @Description: 消息日志
 */
@Data
@ApiModel("消息日志")
@Accessors(chain = true)
public class UserMessageLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty(value = "消息类型", notes = "可选类型在SendMessageTypeEnum枚举")
    private String sendMessageType;
}
