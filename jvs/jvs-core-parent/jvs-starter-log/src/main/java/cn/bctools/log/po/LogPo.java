package cn.bctools.log.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@ApiModel(value = "AOP日志")
@TableName(value = "sys_log", autoResultMap = true)
public class LogPo implements Serializable {
    String id;
    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名")
    String businessName;

    @ApiModelProperty(value = "操作类型")
    String operationType;
    /**
     * 功能名
     */
    @ApiModelProperty(value = "功能名")
    String functionName;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endTime;
    /**
     * 方法名
     */
    @ApiModelProperty(value = "方法名")
    String methodName;
    /**
     * 类名
     */
    @ApiModelProperty(value = "类名")
    String className;
    /**
     * 当前用户对象
     */
    @ApiModelProperty(value = "当前用户对象")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    Object threadUser;
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    String userName;
    @ApiModelProperty(value = "用户ID")
    String userId;
    /**
     * 请求参数对象
     */
    @ApiModelProperty(value = "请求参数对象")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    Object[] parameters;
    /**
     * TID
     */
    @ApiModelProperty(value = "TID")
    String tid;
    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    String version;
    /**
     * 消耗的时间
     */
    @ApiModelProperty(value = "消耗的时间")
    Long consumingTime;

    /**
     * 返回对象
     */
    @ApiModelProperty(value = "返回对象")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    Object returnObj;
    /**
     * 返回状态是否是成功
     */
    @ApiModelProperty(value = "返回状态是否是成功")
    Boolean status;
    /**
     * 请求ip地址
     */
    @ApiModelProperty(value = "请求ip地址")
    String ip;
    /**
     * 异常数据
     */
    @ApiModelProperty(value = "异常数据")
    String elements;
    /**
     * 异常消息数据
     */
    @ApiModelProperty(value = "异常消息数据")
    String exceptionMessage;
    /**
     * 环境参数
     */
    @ApiModelProperty(value = "环境参数")
    String env;
    /**
     * api地址
     */
    @ApiModelProperty(value = "api地址")
    String api;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    String tenantId;

    @ApiModelProperty(value = "终端")
    String clientId;
    @ApiModelProperty(value = "终端名称")
    String clientName;
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createDate;
    /**
     * 1、AOP拦截 默认值  有用户名的操作
     * 2、过程中的日志，   用户没有登录，比如BIZ服务的
     * 3、自定义日志发送
     */
    @ApiModelProperty(value = "日志类型：1-AOP拦截，2-程中的日志，3-自定义日志发送")
    Integer type = 1;

}
