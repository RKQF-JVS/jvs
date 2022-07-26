package cn.bctools.oss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * [Description] :
 *
 * @author : zqs
 * @version : 1.0
 */
@ApiModel("文件信息")
@Data
@Accessors(chain = true)
public class FileNameDto implements Serializable {
    private static final long serialVersionUID = 5216665175529677971L;
    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    String fileName;
    /**
     * 原文件名称
     */
    @ApiModelProperty("原文件名称")
    String originalFileName;
    /**
     * 桶名称
     */
    @ApiModelProperty("桶名称")
    String bucketName;
    /**
     * 时间
     */
    @ApiModelProperty("时间")
    Date date;

    /**
     * 上传时间
     */
    @ApiModelProperty("开始上传时间")
    Date startTime;

    /**
     * 上传时间
     */
    @ApiModelProperty("截至上传时间")
    Date endTime;
    /**
     * 文件外链
     */
    @ApiModelProperty("文件外链")
    String fileLink;

}
