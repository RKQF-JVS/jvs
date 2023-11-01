package cn.bctools.auth.entity;

import cn.bctools.gateway.entity.TenantPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.bctools.auth.entity.enums.SexTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户表
 *
 * @author Administrator
 * @author
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user", autoResultMap = true)
public class User {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    private String id;
    @ApiModelProperty(value = "用户真名")
    private String realName;
    @ApiModelProperty(value = "邮件")
    private String email;
    @ApiModelProperty(value = "性别 [male,female]")
    private SexTypeEnum sex;
    @ApiModelProperty(value = "登录帐号名全库唯一")
    private String accountName;
    @ApiModelProperty(value = "头像")
    private String headImg;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "邀请用户id")
    private String invite;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "0-正常，1- 注销  不要逻辑删除，删除后，业务找不到数据")
    private Boolean cancelFlag;
    @ApiModelProperty(value = "用户类型，1后端用户2前端用户3其他业务系统用户")
    private UserTypeEnum userType;
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;

    @TableField(exist = false)
    private List<TenantPo> tenantPo;

}
