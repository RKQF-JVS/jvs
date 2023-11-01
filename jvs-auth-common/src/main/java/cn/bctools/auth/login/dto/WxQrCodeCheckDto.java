package cn.bctools.auth.login.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * [description]： 前端验证二维码是否扫码成功时的返回值
 *
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class WxQrCodeCheckDto {
    @ApiModelProperty(value = "验证是否通过")
    private Boolean checkStatus = false;
    @ApiModelProperty(value = "是否过期")
    private Boolean isPastDue = false;
}
