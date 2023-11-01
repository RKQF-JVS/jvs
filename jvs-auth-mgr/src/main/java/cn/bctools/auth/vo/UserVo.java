package cn.bctools.auth.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.bctools.auth.entity.enums.SexTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class UserVo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键ID")
    String id;
    @ApiModelProperty(value = "用户真名")
    String realName;

    @ApiModelProperty(value = "邮件")
    String email;
    @ApiModelProperty(value = "性别 [male,female]")
    SexTypeEnum sex;
    @ApiModelProperty(value = "用户帐号")
    String accountName;
    @ApiModelProperty(value = "头像")
    String headImg;
    @ApiModelProperty(value = "手机号")
    String phone;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime;
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateTime;
    @ApiModelProperty(value = "0-正常，1- 注销  不要逻辑删除，删除后，业务找不到数据")
    Boolean cancelFlag;
    @ApiModelProperty(value = "用户类型，1后端用户2前端用户3其他业务系统用户")
    UserTypeEnum userType;
    @ApiModelProperty(value = "生日")
    LocalDate birthday;


    /**租户用户部分*/
    @ApiModelProperty(value = "用户ID")
    String userId;
    @ApiModelProperty(value = "职工编号")
    String employeeNo;
    @ApiModelProperty(value = "帐号等级")
    String level;
    @ApiModelProperty(value = "岗位ID")
    String jobId;
    @ApiModelProperty(value = "岗位名称")
    String jobName;
    @ApiModelProperty(value = "部门ID")
    String deptId;
    @ApiModelProperty(value = "部门名称")
    String deptName;
    @ApiModelProperty(value = "租户ID")
    String tenantId;
}
