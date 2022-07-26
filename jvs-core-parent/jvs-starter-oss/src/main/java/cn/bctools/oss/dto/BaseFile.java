package cn.bctools.oss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 〈文件基本信息〉
 *
 * @since: 1.0.0
 * @author: auto
 */
@Data
@ApiModel("文件基础信息")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseFile {

    /**
     * 桶
     */
    @ApiModelProperty("桶")
    private String bucketName;
    /**
     * 文件名
     */
    @ApiModelProperty("文件名")
    private String fileName;
    /**
     * 原始文件名
     */
    @ApiModelProperty("原始文件名")
    private String originalName;
    /**
     * 文件格式
     */
    private String fileType;
    /**
     * 模块
     */
    private String module;
    /**
     * 文件大小
     */
    private Long size;
}
