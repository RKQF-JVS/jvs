package cn.bctools.common.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限字典扩展
 *
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class DataDictDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源名称")
    String value;
    @ApiModelProperty(value = "资源描述")
    String desc;
    @ApiModelProperty(value = "前端传递是否勾选状态")
    boolean check;
    @ApiModelProperty(value = "下级资源")
    List<DataDictDto> dataDictDto;

}
