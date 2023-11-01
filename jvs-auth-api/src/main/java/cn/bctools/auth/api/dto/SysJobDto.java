package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统岗位信息
 *
 * @author: GuoZi
 */
@Data
@ApiModel("系统岗位信息")
@Accessors(chain = true)
public class SysJobDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("岗位名称")
    private String name;

}
