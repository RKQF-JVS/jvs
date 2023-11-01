package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Administrator
 */
@Data
@ApiModel("树形分类字典")
@Accessors(chain = true)
public class JvsTreeDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("名称, 同一层级不能重复")
    private String name;

    @ApiModelProperty("显示值")
    private String value;

    @ApiModelProperty("分组id, 区分不同的字典树")
    private String groupId;

    @ApiModelProperty("唯一标识, 作为该字典被引用的key")
    private String uniqueName;

    @ApiModelProperty("排序id")
    private Integer sort;

    @ApiModelProperty("备注信息")
    private String remarks;

    @ApiModelProperty("上级Id, 根节点为-1")
    private String parentId;

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("子集")
    private List<JvsTreeDto> children;

}
