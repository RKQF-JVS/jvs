package cn.bctools.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Builder
public class SpaceVo implements Serializable {

    @ApiModelProperty("应用名称")
    String name;

    @ApiModelProperty("存储大小")
    String size;

}
