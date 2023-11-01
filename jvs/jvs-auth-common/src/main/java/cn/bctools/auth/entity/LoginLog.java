package cn.bctools.auth.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author : GaoZeXi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_login_log")
@ApiModel("用户登录行为日志")
public class LoginLog {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("帐号ID")
    private String userId;

    @ApiModelProperty("用户姓名")
    private String realName;

    @ApiModelProperty("登录帐号")
    private String accountName;

    @ApiModelProperty("登录方式 (微信/手机号/扫码/密码)")
    private String loginType;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("操作时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime operateTime;

    @ApiModelProperty("设备来源")
    private String device;

    @ApiModelProperty("消息头")
    private String userAgent;

    @ApiModelProperty("登录状态")
    private Boolean status;
    /**
     * 记录登录终端
     */
    private String clientId;
    private String clientName;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty(value = "公司全称")
    private String tenantName;
    @ApiModelProperty(value = "系统名称")
    private String tenantShortName;
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

}
