package cn.bctools.auth.entity;

import cn.bctools.database.entity.po.BasalPo;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 菜单权限表
 *
 * @author auto
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "sys_menu")
public class Menu extends BasalPo implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "菜单ID")
    private String id;
    @NotEmpty
    @ApiModelProperty(value = "菜单名称")
    private String name;
    @ApiModelProperty(value = "前端URL", notes = "只有部分菜单有此功能")
    private String url;
    @ApiModelProperty(value = "客户端ID")
    private String applyId;
    @ApiModelProperty(value = "父菜单ID, 顶级菜单的父级id与applyId一致")
    private String parentId;
    @ApiModelProperty(value = "菜单的层数")
    private Integer layer;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "默认排序值(用户可搜藏后自定义重新排序)")
    private Integer sort;
    @ApiModelProperty(value = "是否新窗口打开", example = "测试示例值")
    private Boolean newWindow;
    @TableLogic
    @ApiModelProperty(value = "逻辑删除")
    private Boolean delFlag;

}
