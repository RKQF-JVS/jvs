package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.bctools.auth.entity.enums.DictTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author guojing
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_dict")
public class SysDict {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "字典编号")
    private String id;

    /**
     * 类型
     */
    @ApiModelProperty(value = "字典类型")
    private String type;
    /**
     * 是否是系统内置
     */
    @ApiModelProperty(value = "是否系统内置")
    private DictTypeEnum system;
    /**
     * 描述
     */
    @ApiModelProperty(value = "字典描述")
    private String description;
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remarks;

    /**
     * 删除标记
     */
    @TableLogic
    @ApiModelProperty(value = "删除标记,1:已删除,0:正常")
    private Boolean delFlag;

    @ApiModelProperty(value = "唯一id")
    private String uniqId;

}
