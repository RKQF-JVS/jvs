package cn.bctools.auth.component.cons;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class SaveUserDto {

    @ApiModelProperty("标题")
    String title;
    @ApiModelProperty("时间")
    String date;
    @ApiModelProperty("租户名称")
    String tenant;
    @ApiModelProperty("内容")
    String content;
    @ApiModelProperty("用户")
    String user;
    @ApiModelProperty("公司图片")
    String img;


}
