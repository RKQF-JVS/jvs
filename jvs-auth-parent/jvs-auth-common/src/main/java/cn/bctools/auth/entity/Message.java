package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.bctools.auth.entity.enums.SendMessageTypeEnum;
import cn.bctools.auth.entity.enums.SendTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 发送日志
 * </p>
 *
 * @author guojing
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "sys_message", autoResultMap = true)
@ApiModel("消息日志")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 发送类型
     */
    @ApiModelProperty("消息类型")
    private SendMessageTypeEnum sendMessageType;
    /**
     * 发送状态
     */
    @ApiModelProperty("发送状态(0失败;1成功;2未发送;3发送中)")
    private Integer status;
    /**
     * 发送次数
     */
    @ApiModelProperty("发送次数")
    private Integer sendCount;
    /**
     * 发送类型
     */
    @ApiModelProperty("发送类型")
    private SendTypeEnum sendType;
    @ApiModelProperty("消息来源,默认为创建人")
    private String source;
    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;
    /**
     * 批量收件人,最多500个，多余500会切分，可能是用户id，可能是用户手机号
     */
    @ApiModelProperty("收件人")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> recipients;
    /**
     * 发送内容
     */
    @ApiModelProperty("内容")
    private String content;
    /**
     * 发送返回信息
     */
    @ApiModelProperty("发送返回信息,邮件和短信发送的消息")
    private String other;
    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 用于业务扩展字段方便排查问题,存储格式为json
     */
    @ApiModelProperty("用于业务扩展字段方便排查问题")
    private String extension;
    /**
     * 业务id
     */
    @ApiModelProperty("业务id")
    private Integer businessId;
    /**
     * 调用方的业务系统名
     */
    @ApiModelProperty("调用的业务系统名")
    private String businessName;

    @ApiModelProperty("添加逻辑删除标记")
    @TableLogic
    private Boolean delFlag;


}
