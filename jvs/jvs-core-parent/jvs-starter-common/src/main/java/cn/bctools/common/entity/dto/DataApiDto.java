package cn.bctools.common.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class DataApiDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("请求方法")
    String type;
    @ApiModelProperty("请求地址")
    String api;

}
