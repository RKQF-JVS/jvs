package cn.bctools.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.bctools.common.entity.dto.DataDictDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author guojing
 */
@Data
@Accessors(chain = true)
@TableName(value = "sys_data")
public class SysData {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.ASSIGN_UUID)
    String id;
    @ApiModelProperty("请求方法")
    String type;
    @ApiModelProperty("数据表的中文名称")
    String name;
    @ApiModelProperty("请求地址")
    String api;
    @ApiModelProperty("描述")
    String describes;
    @ApiModelProperty("功能名称")
    String functionName;

    String remark;

    /**
     * 排除此字段
     */
    @TableField(exist = false)
    List<DataDictDto> dataList;

}
