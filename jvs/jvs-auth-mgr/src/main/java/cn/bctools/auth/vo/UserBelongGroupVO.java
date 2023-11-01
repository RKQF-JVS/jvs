package cn.bctools.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ZhuXiaoKang
 * @Description: 用户所属组集合
 */

@Data
@Accessors(chain = true)
@ApiModel("用户所属组集合")
public class UserBelongGroupVO {

    @ApiModelProperty(value = "组id")
    private String id;

    @ApiModelProperty(value = "组名")
    private String name;

    @ApiModelProperty(value = "组用户总数")
    private Integer userTotal;
}
