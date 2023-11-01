package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户表
 *
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user_tenant", autoResultMap = true)
public class UserTenant {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    String id;
    @ApiModelProperty(value = "用户ID")
    String userId;
    @ApiModelProperty(value = "用户真名")
    String realName;
    @ApiModelProperty(value = "手机")
    String phone;
    @ApiModelProperty(value = "职工编号")
    String employeeNo;
    @ApiModelProperty(value = "帐号等级")
    String level;
    @ApiModelProperty(value = "岗位ID")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String jobId;
    @ApiModelProperty(value = "岗位名称")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String jobName;
    @ApiModelProperty(value = "部门ID")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String deptId;
    @ApiModelProperty(value = "部门名称")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String deptName;
    @ApiModelProperty(value = "租户ID")
    String tenantId;
    @ApiModelProperty(value = "0-正常，1-注销  不要逻辑删除，删除后，业务找不到数据")
    private Boolean cancelFlag;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime;


}
