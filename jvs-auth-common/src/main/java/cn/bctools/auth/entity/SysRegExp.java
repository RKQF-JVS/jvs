package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author : GaoZeXi
 */
@Data
@ApiModel(value = "正则管理")
@Accessors(chain = true)
@TableName("sys_regexp")
public class SysRegExp {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "唯一名称,该名称为该字典创建时name的值,且该值永不改变,作为该字典被引用的key")
    private String uniqueName;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotNull(message = "名称不允许为空")
    @Size(max = 20, message = "名称最大长度为20")
    private String name;
    /**
     * 显示值
     */
    @ApiModelProperty(value = "表达式")
    @NotNull(message = "表达式不允许为空")
    @Size(max = 999, message = "表达式超出长度限制")
    private String expression;
    /**
     * 自定义分类
     */
    @ApiModelProperty(value = "自定义分类")
    @NotNull(message = "分类不允许为空")
    private String type;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remarks;

}
