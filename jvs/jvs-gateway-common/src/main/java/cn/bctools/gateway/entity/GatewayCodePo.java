package cn.bctools.gateway.entity;

import cn.bctools.gateway.entity.enums.MatchingMethodEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty("具体错误码确定")
    @TableField("code")
    private Integer code;
    @ApiModelProperty("错误信息确定，与代码保持一致")
    @TableField("msg")
    private String msg;
    @ApiModelProperty("其它信息备注")
    @TableField("remark")
    private String remark;
    @ApiModelProperty("匹配方式")
    private MatchingMethodEnum matchingMethod;
}
