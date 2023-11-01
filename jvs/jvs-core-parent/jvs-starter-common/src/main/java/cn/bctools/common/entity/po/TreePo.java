package cn.bctools.common.entity.po;

import com.alibaba.fastjson.JSONObject;
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
public class TreePo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单ID")
    private String id;
    @ApiModelProperty(value = "父菜单ID(只支持一层父级),为-1时为最上级")
    private String parentId;
    @ApiModelProperty(value = "默认排序值(用户可收藏后自定义重新排序)")
    public Integer sort;
    @ApiModelProperty(value = "菜单名称")
    private String name;
    @ApiModelProperty(value = "应用ID")
    private String jvsAppId;
    @ApiModelProperty(value = "其它字段")
    private Object extend;
    @ApiModelProperty(value = "权限")
    private List<JSONObject> role;
    /**
     * 该字段仅三级菜单使用
     */
    @ApiModelProperty(value = "是否为最近30天内创建", example = "true")
    private Boolean isNew;

}
