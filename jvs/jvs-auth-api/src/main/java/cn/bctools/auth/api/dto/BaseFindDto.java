package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BaseFindDto {
    @ApiModelProperty("用户ID集")
    List<String> userIds;
    @ApiModelProperty("角色ID集")
    List<String> roleIds;
    @ApiModelProperty("部门id集")
    List<String> deptIds;
    @ApiModelProperty("岗位ID集")
    List<String> jobIds;
    @ApiModelProperty("群组ID集")
    List<String> groupIds;
}
