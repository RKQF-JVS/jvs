package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户与资源权限的权限关联
 *
 * @author 
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user_permission")
public class UserPermission implements Serializable {

    @ApiModelProperty(value = "用户ID")
    @TableId(type = IdType.INPUT)
    private String userId;

    @ApiModelProperty(value = "资源id")
    private String permissionId;

    @ApiModelProperty(value = "用户自定义别名(用户授权后会自动将菜单名称同步到此)")
    private String alias;

}
