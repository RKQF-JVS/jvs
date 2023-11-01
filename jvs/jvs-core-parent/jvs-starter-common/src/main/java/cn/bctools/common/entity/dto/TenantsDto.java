package cn.bctools.common.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TenantsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String shortName;
    private String icon;

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
    @ApiModelProperty(value = "logo")
    private String logo;
    @ApiModelProperty(value = "公司全称")
    private String name;
    @ApiModelProperty(value = "登录域名映射")
    private String loginDomain;
    public TenantsDto(String id, String shortName, String icon, String logo, String name) {
        this.id = id;
        this.shortName = shortName;
        this.icon = icon;
        this.logo = logo;
        this.name = name;
    }
}
