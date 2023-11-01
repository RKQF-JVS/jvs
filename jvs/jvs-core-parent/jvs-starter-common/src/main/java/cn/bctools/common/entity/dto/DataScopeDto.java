package cn.bctools.common.entity.dto;

import cn.bctools.common.enums.DataScopeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限隔离对象,获取当前用户的数据权限功能。可支持角色，岗位，下级等。
 *
 * @author auto
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DataScopeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("数据权限类型")
    DataScopeType dataScopeType;

    /**
     * 创建人id
     */
    List<String> createByIds = new ArrayList<>();
    /**
     * 部门
     */
    List<String> deptIds = new ArrayList<>();
    /**
     * 岗位
     */
    List<String> jobIds = new ArrayList<>();
    /**
     * 自定义字段
     */
    List<DataDictDto> dataDicts = new ArrayList<>();
    /**
     * 该数据权限对应的API
     */
    DataApiDto dataApi = new DataApiDto();

}
