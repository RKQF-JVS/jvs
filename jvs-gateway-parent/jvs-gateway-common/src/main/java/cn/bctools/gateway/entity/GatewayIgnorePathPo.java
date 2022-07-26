package cn.bctools.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName(value = "sys_gateway_ignore_path")
public class GatewayIgnorePathPo {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty("请求路径")
    private String path;
    @ApiModelProperty("备注")
    private String remark;

}
