package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 
 */
@Data
@Accessors(chain = true)
public class ApplyDto {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "应用名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String describes;
    @ApiModelProperty(value = "应用key")
    private String appKey;
}
