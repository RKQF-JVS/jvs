package cn.bctools.auth.vo;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@ApiModel("应用菜单")
public class ApplyMenuVo {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "应用名称")
    private String name;
    @ApiModelProperty(value = "菜单")
    List<Tree<Object>> menus;

}
