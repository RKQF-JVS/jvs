package cn.bctools.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class InviteVo {

    @ApiModelProperty("显示的链接图片")
    String base64;

    @ApiModelProperty("可点击的邀请码地址")
    String url;

}
