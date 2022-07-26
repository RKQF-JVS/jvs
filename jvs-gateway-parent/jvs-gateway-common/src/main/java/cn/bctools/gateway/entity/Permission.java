package cn.bctools.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_permission", autoResultMap = true)
public class Permission implements Serializable {

    @ApiModelProperty(value = "资源标识")
    @TableId(type = IdType.ASSIGN_UUID)
    private String permission;
    @ApiModelProperty(value = "请求地址")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> api;

}
