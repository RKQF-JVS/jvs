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
import java.util.Map;

/**
 * 用户表 三方扩展信息表
 *
 * @author Administrator
 * @author
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_user_extension", autoResultMap = true)
public class UserExtension {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    String id;
    @ApiModelProperty("三方类型，对应loginHandler")
    String type;
    String openId;
    @ApiModelProperty("用户ID")
    String userId;
    @ApiModelProperty("昵称")
    String nickname;

    @ApiModelProperty("三方扩展信息其它字段")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    Map<String, Object> extension;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    Map<String, Object> remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}
