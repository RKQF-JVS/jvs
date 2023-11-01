package cn.bctools.message.push.entity;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.database.entity.po.BasalPo;
import cn.bctools.message.push.dto.enums.InsideNotificationTypeEnum;
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
 * 站内消息通知
 * </p>
 *
 * @author wl
 * @since 2022-06-07
 */
@EqualsAndHashCode(callSuper = true)
@TableName("inside_notice")
@Data
@Accessors(chain = true)
@ApiModel("站内消息通知")
public class InsideNotice extends BasalPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    @ApiModelProperty("id")
    private String id;

    /**
     * 用户id
     */
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private String userId;

    /**
     * 用户名称
     */
    @TableField("user_name")
    @ApiModelProperty("用户名称")
    private String userName;

    /**
     * 消息内容
     */
    @TableField("msg_content")
    @ApiModelProperty("消息内容")
    private String msgContent;

    /**
     * 是否已读
     */
    @TableField("read_is")
    @ApiModelProperty("是否已读")
    private Boolean readIs;

    /**
     * 批次号
     */
    @TableField("batch_number")
    @ApiModelProperty("消息发送批次号")
    private String batchNumber;

    /**
     * 客户终端唯一标识
     */
    @TableField("client_code")
    @ApiModelProperty("客户终端唯一标识")
    private String clientCode;

    /**
     * 客户终端名称
     */
    @TableField(exist = false)
    @ApiModelProperty("客户终端名称")
    private String clientName;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    @ApiModelProperty("用户信息")
    private UserDto userDto;

    @TableField("del_flag")
    @ApiModelProperty("是否删除")
    private Boolean delFlag;

    @TableField("tenant_id")
    @ApiModelProperty("租户id")
    private String tenantId;

    @TableField("large_categories")
    @ApiModelProperty("大类")
    private InsideNotificationTypeEnum largeCategories;

    @TableField("sub_class")
    @ApiModelProperty("子类")
    private String subClass;

    @TableField("call_back_url")
    @ApiModelProperty("回调地址")
    private String callBackUrl;

    @TableField("notice_avatar")
    @ApiModelProperty("站内信头像")
    private String noticeAvatar;


}
