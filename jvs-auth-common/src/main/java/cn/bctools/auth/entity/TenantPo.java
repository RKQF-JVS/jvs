package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author guojing
 * 公司租户管理
 */
@Data
@TableName(value = "sys_tenant")
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
    private String defaultIndexUrl;
    @ApiModelProperty(value = "ICON")
    private String icon;
    @ApiModelProperty(value = "logo")
    private String logo;
    @ApiModelProperty(value = "bgImg")
    private String bgImg;
    @ApiModelProperty(value = "管理员ID字段")
    private String adminUserId;
}
