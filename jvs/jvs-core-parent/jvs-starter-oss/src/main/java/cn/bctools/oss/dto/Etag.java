package cn.bctools.oss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <分段>
 *
 * @author auto
 **/
@ApiModel("文件分段信息")
@Data
@Accessors(chain = true)
public class Etag {
    /**
     * 分片序号
     */
    @ApiModelProperty("分片序号")
    private Integer partNumber;
    /**
     * etag
     */
    @ApiModelProperty("etag")
    private String etag;
    /**
     * 上传凭证
     */
    @ApiModelProperty("上传凭证")
    private String uploadId;
    /**
     * 桶名
     */
    @ApiModelProperty("桶名")
    private String bucketName;
    /**
     * 原始文件名
     */
    @ApiModelProperty("原始文件名")
    private String originFileName;
    /**
     * 外链文件名
     */
    @ApiModelProperty("外链文件名")
    private String fileName;
    /**
     * 总分段数量
     */
    @ApiModelProperty("总分段数量")
    private Integer totalPartNumber;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Long createTime;
}
