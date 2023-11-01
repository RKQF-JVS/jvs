package cn.bctools.auth.entity;

import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Auto Generator
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_config", autoResultMap = true)
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    private String id;
    @ApiModelProperty(value = "租户id")
    private String jvsTenantId;
    @ApiModelProperty(value = "系统应用id")
    private String appId;
    @ApiModelProperty(value = "配置类型")
    private SysConfigTypeEnum type;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("是否无权限访问")
    private Boolean role;
    @ApiModelProperty("保存对象")
    @TableField(typeHandler = JsonStrTypeHandler.class)
    private Object content;


}
