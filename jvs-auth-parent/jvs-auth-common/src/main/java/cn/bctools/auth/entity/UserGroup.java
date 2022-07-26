package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户组
 *
 * @author auto
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user_group", autoResultMap = true)
public class UserGroup {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * json字符串, 例: ["1","2"]
     */
    @ApiModelProperty("用户数据对象")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> users;

    @ApiModelProperty("用户组名称")
    private String name;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
