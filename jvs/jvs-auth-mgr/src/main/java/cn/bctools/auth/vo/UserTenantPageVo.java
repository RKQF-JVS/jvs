package cn.bctools.auth.vo;

import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SexTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ZhuXiaoKang
 * @Description: 用户列表
 */
@Data
@ApiModel("用户列表入参")
public class UserTenantPageVo extends UserTenant {

    @ApiModelProperty(value = "账号")
    private String accountName;

    @ApiModelProperty(value = "性别 [male,female]")
    private SexTypeEnum sex;

}
