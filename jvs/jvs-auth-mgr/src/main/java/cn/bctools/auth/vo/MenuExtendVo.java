package cn.bctools.auth.vo;

import cn.bctools.auth.entity.Menu;
import cn.bctools.gateway.entity.Permission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 */
@Data
@Accessors(chain = true)
@ApiModel("菜单扩展数据")
public class MenuExtendVo {

    @ApiModelProperty(value = "菜单信息")
    private Menu menu;
    @ApiModelProperty(value = "资源信息")
    List<Permission> permissions;
}
