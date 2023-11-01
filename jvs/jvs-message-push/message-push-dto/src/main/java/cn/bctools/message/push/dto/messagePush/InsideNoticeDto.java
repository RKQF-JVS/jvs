package cn.bctools.message.push.dto.messagePush;

import cn.bctools.common.entity.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@ApiModel("站内消息通知")
public class InsideNoticeDto {

    @ApiModelProperty("id")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String userName;

    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    private String msgContent;

    /**
     * 是否已读
     */
    @ApiModelProperty("是否已读")
    private Boolean readIs;

    /**
     * 批次号
     */
    @ApiModelProperty("消息发送批次号")
    private String batchNumber;

    /**
     * 客户终端唯一标识
     */
    @ApiModelProperty("客户终端唯一标识")
    private String clientCode;

    /**
     * 客户终端名称
     */
    @ApiModelProperty("客户终端名称")
    private String clientName;

    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private UserDto userDto;

    @ApiModelProperty("创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人id")
    private String createById;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("修改人")
    private String updateBy;
}
