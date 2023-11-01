package cn.bctools.message.push.entity;

import cn.bctools.database.entity.po.BasalPo;
import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.dto.enums.MessageTypeEnum;
import cn.bctools.message.push.dto.enums.PlatformEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 消息发送历史
 * </p>
 *
 * @author wl
 * @since 2022-05-18
 */
@EqualsAndHashCode(callSuper = false)
@TableName("message_push_his")
@Data
@Accessors(chain = true)
@ApiModel("消息历史")
public class MessagePushHis extends BasalPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    @ApiModelProperty("消息历史id")
    private String id;

    /**
     * 批次号
     */
    @TableField("batch_number")
    @ApiModelProperty("消息发送批次号")
    private String batchNumber;

    /**
     * 平台
     */
    @TableField("platform")
    @ApiModelProperty("消息平台")
    private PlatformEnum platform;

    /**
     * 消息类型
     */
    @TableField("message_type")
    @ApiModelProperty("消息类型")
    private MessageTypeEnum messageType;

    /**
     * 消息内容
     */
    @TableField("message_content")
    @ApiModelProperty("消息内容")
    private String messageContent;


    /**
     * 消息发送状态
     */
    @TableField("push_status")
    @ApiModelProperty("消息发送状态")
    private MessagePushStatusEnum pushStatus;

    /**
     * 异常消息
     */
    @TableField("error_msg")
    @ApiModelProperty("异常消息")
    private String errorMsg;

    /**
     * 接收人用户id
     */
    @TableField("user_id")
    @ApiModelProperty("接收人用户id jvs")
    private String userId;

    /**
     * 接收人用户名称
     */
    @TableField("user_name")
    @ApiModelProperty("接收人用户名称 jvs")
    private String userName;

    /**
     * 客户端标识
     */
    @TableField("client_code")
    @ApiModelProperty("客户端标识")
    private String clientCode;

    /**
     * 租户id
     */
    @TableField("tenant_id")
    @ApiModelProperty("租户id")
    private String tenantId;

}
