package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 字典项
 *
 * @author guojing
 */
@Data
@ApiModel(value = "字典项")
@Accessors(chain = true)
@TableName(value = "sys_dict_item", autoResultMap = true)
public class SysDictItem {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.ASSIGN_UUID)
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
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> remarks;

    /**
     * 删除标记
     */
    @TableLogic
    @ApiModelProperty(value = "删除标记,1:已删除,0:正常")
    private Boolean delFlag;

}
