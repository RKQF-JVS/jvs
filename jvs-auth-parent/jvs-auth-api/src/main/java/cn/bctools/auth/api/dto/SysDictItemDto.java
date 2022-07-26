package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <字典项>
 *
 * @author auto
 **/
@ApiModel(value = "字典项")
@Data
@Accessors(chain = true)
public class SysDictItemDto {

    /**
     * 编号
     */
    @ApiModelProperty(value = "字典项id")
    private String id;

    /**
     * 所属字典类id
     */
    @ApiModelProperty(value = "所属字典类id")
    private String dictId;

    /**
     * 数据值
     */
    @ApiModelProperty(value = "数据值")
    private String value;

    /**
     * 标签名
     */
    @ApiModelProperty(value = "标签名")
    private String label;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 排序（升序）
     */
    @ApiModelProperty(value = "排序值，默认升序")
    private Integer sort;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remarks;

}
