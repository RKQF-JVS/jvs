package cn.bctools.message.push.dto.messagePush;

import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息发送历史
 * </p>
 *
 * @author wl
 * @since 2022-05-18
 */
@Data
@Accessors(chain = true)
@ApiModel("消息历史")
public class MessagePushHisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息历史id")
    private String id;

    /**
     * 批次号
     */
    @ApiModelProperty("消息发送批次号")
    private String batchNumber;

    /**
     * 平台
     */
    @ApiModelProperty("消息平台")
    private PlatformEnum platform;

    /**
     * 消息发送平台 中文名称
     */
    @ApiModelProperty("消息发送平台 中文名称")
    private String platformName;

    /**
     * 消息类型
     */
    @ApiModelProperty("消息类型")
    private MessageTypeEnum messageType;

    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    private String messageContent;

    /**
     * 消息发送状态
     */
    @ApiModelProperty("消息发送状态")
    private MessagePushStatusEnum pushStatus;

    /**
     * 消息发送状态 中文名称
     */
    @ApiModelProperty("消息发送状态 中文名称")
    private String pushStatusName;

    /**
     * 异常消息
     */
    @ApiModelProperty("异常消息")
    private String errorMsg;

    /**
     * 接收人用户id
     */
    @ApiModelProperty("接收人用户id jvs")
    private String userId;

    /**
     * 接收人用户名称
     */
    @ApiModelProperty("接收人用户名称 jvs")
    private String userName;

    /**
     * 客户端标识
     */
    @ApiModelProperty("客户端标识")
    private String clientCode;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人")
    private String createById;

    @ApiModelProperty("创建人民成功")
    private String createBy;

    @ApiModelProperty("修改人")
    private String updateBy;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("查询开始时间")
    private LocalDateTime queryStartTime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("查询结束时间")
    private LocalDateTime queryEndTime;

}
