package cn.bctools.common.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户基础对象，所有的用户信息都默认都使用此对象进行传递，所有微服务中的登录用户，也都使用此对象进行操作
 *
 * @author gj
 */
@Data
@Accessors(chain = true)
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户主键ID
     */
    private String id;

    private String realName;

    private String email;
    /**
     * 用户名，默认为登录用户名
     */
    private String accountName;
    private String headImg;
    private String phone;
    private String sex;
    private String jobId;
    private String jobName;
    private String deptId;
    private String deptName;
    private String password;
    private Boolean cancelFlag;
    private String tenantId;
    /**是否是超级管理员*/
    private Boolean adminFlag;
    private String employeeNo;
    private Integer level;
    private String userAgent;
    private String loginType;
    /**
     * 记录登录终端名
     */
    private String clientName;
    /**
     * 记录登录终端
     */
    private String clientId;
    /**
     * 三方回调  携带整个用户对象信息
     */
    private String callBackUrl;
    private String ip;
    private List<TenantsDto> tenants;
    @ApiModelProperty("当前选择租户的详细信息")
    private TenantsDto tenant;
    @ApiModelProperty("用户的扩展信息")
    private Map<String, Object> exceptions;

}
