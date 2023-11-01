package cn.bctools.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Auto Generator
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("网关不加密接口，主要是mgr的地址")
@EqualsAndHashCode(callSuper = false)
@TableName("sys_gateway_ignore_encode")
public class GatewayIgnoreEncode implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty("")
    @TableField("path")
    private String path;
    @ApiModelProperty("备注说明")
    @TableField("remark")
    private String remark;
}
