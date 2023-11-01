package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户等级,每一个用户等级，一个首页
 *
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user_level")
public class UserLevel {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    String id;
    @ApiModelProperty(value = "等级名称")
    String name;
    @ApiModelProperty(value = "排序")
    Integer sort;
    @ApiModelProperty(value = "首页地址")
    String indexUrl;


}
