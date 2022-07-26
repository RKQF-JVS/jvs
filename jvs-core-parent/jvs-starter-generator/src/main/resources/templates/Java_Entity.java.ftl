package ${rootPkg}.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Auto Generator
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("${tableInfo}")
@EqualsAndHashCode(callSuper = false)
@TableName("${tableName}")
public class ${entityName} implements Serializable {

    private static final long serialVersionUID = 1L;
<#list tableFields as item>
    @ApiModelProperty("${item.desc}")
<#if item.isPri==false>
    @TableField("${item.fieldName}")
<#elseif item.type=='String'>
    @TableId(value = "${item.fieldName}", type = IdType.ASSIGN_UUID)
<#else>
    @TableId(value = "${item.fieldName}", type = IdType.AUTO)
</#if>
<#if item.isLogicDel==true>
    @TableLogic
</#if>
<#if item.timeType=='LocalDateTime'>
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
<#elseif item.timeType=='LocalTime'>
    @DateTimeFormat(pattern = DatePattern.NORM_TIME_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_TIME_PATTERN, timezone = "GMT+8")
<#elseif item.timeType=='LocalDate'>
    @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
<#elseif item.timeType=='Date'>
    @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
</#if>
    private ${item.type} ${item.name};
</#list>
}
