//package cn.bctools.message.push.entity;
//
//import cn.bctools.database.entity.po.BasalPo;
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
// * 消息配置--客户端
// * </p>
// *
// * @author wl
// * @since 2022-05-18
// */
//@EqualsAndHashCode(callSuper = false)
//@TableName("message_config")
//@Data
//@Accessors(chain = true)
//@ApiModel("消息配置--客户端")
//public class MessageConfig extends BasalPo implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id",type = IdType.ASSIGN_UUID)
//    private String id;
//
//    /**
//     * 客户端唯一标识
//     */
//    @TableField("client_code")
//    @ApiModelProperty("客户端唯一标识")
//    private String clientCode;
//
//    /**
//     * 客户端名称
//     */
//    @TableField("client_name")
//    @ApiModelProperty("客户端名称")
//    private String clientName;
//}
