package cn.bctools.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class TenantSpaceVo implements Serializable {

    @ApiModelProperty("文件存储大小")
    String fileSumSize;
    @ApiModelProperty("数据存储大小")
    String dataSumSize;
    @ApiModelProperty("分布")
    List<SpaceVo> list;

    //    @ApiModelProperty("文件存储大小")
//    String appSize;
//    @ApiModelProperty("公共文件大小")
//    String publicSize;
//    @ApiModelProperty("大屏素材大小")
//    String screenSize;
//    @ApiModelProperty("表单存储大小")
//    String formDesignSize;

}
