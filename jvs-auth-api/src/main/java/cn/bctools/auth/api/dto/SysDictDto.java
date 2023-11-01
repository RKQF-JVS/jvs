package cn.bctools.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * <>
 *
 * @author auto
 **/
@Data
@Accessors(chain = true)
public class SysDictDto {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    private String id;
    /**
     * 数据值
     */
    @NotBlank(message = "字典项数据值不能为空")
    @ApiModelProperty(value = "数据值", required = true)
    private String value;
    /**
     * 标签名
     */
    @NotBlank(message = "字典项标签不能为空")
    @ApiModelProperty(value = "标签名", required = true)
    private String label;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", required = true)
    @NotBlank(message = "字典项数据类型不能为空")
    private String type;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remarks;
    @ApiModelProperty(value = "唯一id")
    private String uniqId;

}
