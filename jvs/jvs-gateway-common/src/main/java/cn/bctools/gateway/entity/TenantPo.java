package cn.bctools.gateway.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 
 * 公司租户管理
 */
@Data
@TableName(value = "sys_tenant", autoResultMap = true)
@Accessors(chain = true)
@ApiModel(value = "公司租户管理")
public class TenantPo {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "公司ID")
    private String id;
    @NotBlank(message = "公司全称不能为空")
    @ApiModelProperty(value = "公司全称")
    private String name;
    @ApiModelProperty(value = "登录域名映射")
    private String loginDomain;
    @NotBlank(message = "系统名称不能为空")
    @ApiModelProperty(value = "系统名称")
    private String shortName;
    @ApiModelProperty(value = "启用禁用,禁用后，此下的用户都不可登录和操作")
    private Boolean enable;
    @ApiModelProperty(value = "上级租户ID")
    private String parentId;
    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableLogic
    @ApiModelProperty(value = "是否删除  1：已删除  0：正常", hidden = true)
    @TableField(select = false)
    private Boolean delFlag;
    @ApiModelProperty(value = "公司地址")
    private String addr;
    @ApiModelProperty(value = "公司简介")
    private String descMsg;
    @ApiModelProperty(value = "默认首页地址")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String defaultIndexUrl;
    @ApiModelProperty(value = "ICON")
    private String icon;
    @ApiModelProperty(value = "logo")
    private String logo;
    @ApiModelProperty(value = "bgImg")
    private String bgImg;
    @ApiModelProperty(value = "管理员ID字段")
    private String adminUserId;
    private String adminUserAccount;
    @TableField(typeHandler = FastjsonTypeHandler.class)
    Map<String, Object> remark;
    @ApiModelProperty(value = "用户默认密码", notes = "未设置初始密码，默认123456")
    @TableField(value = "default_password")
    private String defaultPassword = "123456";
    @ApiModelProperty(value = "管理员名称")
    @TableField(exist = false)
    private String adminUserName;
    @ApiModelProperty(value = "管理员头像")
    @TableField(exist = false)
    private String adminUserImg;
}
