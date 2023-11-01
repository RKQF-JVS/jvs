package cn.bctools.auth.entity;

import cn.bctools.auth.api.enums.DeptEnum;
import cn.bctools.auth.entity.enums.AuthSourceEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门管理
 *
 * @author
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_dept")
public class Dept implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键")
    private String id;
    @NotEmpty(message = "部门名称不能为空")
    @ApiModelProperty(value = "部门名称")
    private String name;
    @ApiModelProperty(value = "部门类型，部门，子公司")
    private DeptEnum type;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
    @TableLogic
    @ApiModelProperty(value = "是否删除  1：已删除  0：正常")
    private Boolean delFlag;
    @ApiModelProperty(value = "顶级部门的上级id默认为当前租户id")
    private String parentId;
    @ApiModelProperty(value = "部门负责人Id")
    private String leaderId;
    @ApiModelProperty(value = "排序号")
    private Integer sort;
    @ApiModelProperty(value = "来源")
    private AuthSourceEnum source;
}
