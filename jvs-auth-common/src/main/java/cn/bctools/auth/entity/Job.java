package cn.bctools.auth.entity;

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
 * 岗位管理
 *
 * @author
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_job")
public class Job implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键")
    private String id;
    @NotEmpty
    @ApiModelProperty(value = "岗位名称")
    private String name;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
    @TableLogic
    @ApiModelProperty(value = "是否删除  1：已删除  0：正常")
    private Boolean delFlag;
}
