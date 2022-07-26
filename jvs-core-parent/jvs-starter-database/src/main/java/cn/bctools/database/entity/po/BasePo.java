package cn.bctools.database.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guojing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BasePo extends BasalPo {

    @ApiModelProperty("部门字段")
    @TableField(fill = FieldFill.INSERT)
    private String deptId;
    @ApiModelProperty("岗位字段")
    @TableField(fill = FieldFill.INSERT)
    private String jobId;
}
