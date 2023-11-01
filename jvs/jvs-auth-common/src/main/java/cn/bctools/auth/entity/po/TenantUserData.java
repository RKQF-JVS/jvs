package cn.bctools.auth.entity.po;

import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SexTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Author: ZhuXiaoKang
 * @Description: 租户用户信息
 */
@Data
@Accessors(chain = true)
public class TenantUserData extends UserTenant {

    /**
     * 用户基本信息
     */
    @ApiModelProperty(value = "邮件")
    private String email;
    @ApiModelProperty(value = "性别 [male,female]")
    private SexTypeEnum sex;
    @ApiModelProperty(value = "登录帐号名全库唯一")
    private String accountName;
    @ApiModelProperty(value = "头像")
    private String headImg;
    @ApiModelProperty(value = "邀请用户id")
    private String invite;
    @ApiModelProperty(value = "用户类型，1后端用户2前端用户3其他业务系统用户")
    private UserTypeEnum userType;
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;
}
