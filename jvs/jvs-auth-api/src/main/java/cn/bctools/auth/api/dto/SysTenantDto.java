package cn.bctools.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author: ZhuXiaoKang
 * @Description: 租户信息
 */
@Data
@ApiModel("租户信息")
@Accessors(chain = true)
public class SysTenantDto {

    @ApiModelProperty(value = "公司ID")
    private String id;
    @ApiModelProperty(value = "公司全称")
    private String name;
    @ApiModelProperty(value = "登录域名映射")
    private String loginDomain;
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
    @ApiModelProperty(value = "是否删除  1：已删除  0：正常", hidden = true)
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
