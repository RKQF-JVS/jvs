package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统角色表
 *
 * @author
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_role")
public class Role implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色解释")
    private String roleDesc;
    @ApiModelProperty(value = "tenantRole，租户角色，userRole为系统角色")
    private RoleTypeEnum type;
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    @ApiModelProperty(value = "删除标识（0-正常,1-删除）")
    @TableLogic
    private Boolean delFlag;
    @ApiModelProperty(value = "加入组织后是否自动赋予该权限")
    private Boolean autoGrant;
}
