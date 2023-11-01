package cn.bctools.auth.api.dto;

import cn.bctools.auth.api.enums.PersonnelTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ZhuXiaoKang
 * @Description: 通用的人员选择
 */
@Data
@ApiModel("通用人员选择")
@Accessors(chain = true)
public class PersonnelDto {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private PersonnelTypeEnum type;
}
