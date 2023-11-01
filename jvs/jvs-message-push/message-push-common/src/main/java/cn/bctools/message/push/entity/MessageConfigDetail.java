//package cn.bctools.message.push.entity;
//
//import cn.bctools.database.entity.po.BasalPo;
//import cn.bctools.message.push.dto.config.BaseConfig;
//import cn.bctools.message.push.dto.enums.PlatformEnum;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//
//import java.io.Serializable;
//
///**
// * <p>
// * 配置详情
// * </p>
// *
// * @author wl
// * @since 2022-05-18
// */
//@EqualsAndHashCode(callSuper = false)
//@TableName("message_config_detail")
//@Data
//@Accessors(chain = true)
//@ApiModel("消息配置--配置详情")
//public class MessageConfigDetail extends BasalPo implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id",type = IdType.ASSIGN_UUID)
//    private String id;
//
//    /**
//     * 配置id
//     */
//    @TableField("config_id")
//    @ApiModelProperty("配置id")
//    private String configId;
//
//    /**
//     * 配置id
//     */
//    @TableField("client_code")
//    @ApiModelProperty("终端唯一标识符")
//    private String clientCode;
//
//    /**
//     * 配置详情
//     */
//    @TableField("config_value")
//    @ApiModelProperty("配置详情")
//    private String configValue;
//
//    /**
//     * 平台类型
//     */
//    @TableField("platform")
//    @ApiModelProperty("平台类型")
//    private PlatformEnum platform;
//
//    @TableField(exist = false)
//    @ApiModelProperty("配置详细")
//    private BaseConfig configDetail;
//}
