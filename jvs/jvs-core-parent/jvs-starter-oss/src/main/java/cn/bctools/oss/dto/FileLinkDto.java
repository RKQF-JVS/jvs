package cn.bctools.oss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author auto
 */
@ApiModel("文件信息")
@Data
@Accessors(chain = true)
public class FileLinkDto {
    @ApiModelProperty("桶名")
    private String bucketName;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("原文件名")
    private String originFileName;
    @ApiModelProperty("文件外链")
    private String fileLink;
}
