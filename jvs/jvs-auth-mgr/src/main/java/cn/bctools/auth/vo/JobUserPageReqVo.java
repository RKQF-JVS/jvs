package cn.bctools.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ZhuXiaoKang
 * @Description: 岗位下的用户分页查询
 */
@Data
@ApiModel("岗位下的用户分页查询入参")
public class JobUserPageReqVo {

    @ApiModelProperty(value = "岗位id")
    private String jobId;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String phone;
}
