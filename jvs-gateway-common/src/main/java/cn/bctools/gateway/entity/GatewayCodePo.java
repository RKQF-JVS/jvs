package cn.bctools.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_gateway_code")
public class GatewayCodePo {
    @TableId(type = IdType.ASSIGN_UUID)
    String id;
    @ApiModelProperty("具体错误码确定")
    Integer code;
    @ApiModelProperty("错误信息确定与代码保持一致")
    String msg;
}
