package cn.bctools.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author guojing
 * 公司租户管理
 */
@Data
@TableName(value = "sys_tenant", autoResultMap = true)
@Accessors(chain = true)
@ApiModel(value = "公司租户管理")
public class TenantPo {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value = "登录域名")
    private String loginDomain;
    @ApiModelProperty(value = "ICON")
    private String icon;
    @ApiModelProperty(value = "背景图")
    private String bgImg;
    @ApiModelProperty(value = "LOGO")
    private String logo;
    @ApiModelProperty(value = "公司全称")
    private String name;
    @TableField(typeHandler = FastjsonTypeHandler.class)
    Map<String, Object> remark;
}