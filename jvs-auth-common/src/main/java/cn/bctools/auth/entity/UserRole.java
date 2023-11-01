package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户角色表
 *
 * @author
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user_role")
public class UserRole implements Serializable {
    @ApiModelProperty(value = "用户ID")
    @TableId(type = IdType.INPUT)
    private String userId;
    @ApiModelProperty(value = "角色ID")
    private String roleId;

}
