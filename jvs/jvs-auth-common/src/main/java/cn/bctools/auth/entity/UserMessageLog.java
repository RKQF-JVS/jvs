package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.bctools.auth.entity.enums.SendMessageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 发送日志
 * </p>
 *
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_message_user_log")
@ApiModel("消息日志")
public class UserMessageLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("读取状态")
    private Boolean readStatus;

    @ApiModelProperty("读取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 发送内容
     */
    @ApiModelProperty("内容")
    private String content;

    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("是否隐藏")
    private Boolean hide;

    @ApiModelProperty("来源")
    private String source;

    /**
     * 发送类型
     */
    @ApiModelProperty("消息类型")
    private SendMessageTypeEnum sendMessageType;
    @ApiModelProperty("用户头像")
    @TableField(exist = false)
    private String headImg;
}
