package cn.bctools.auth.login.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class RegisterDto {
    @ApiModelProperty(value = "姓名")
    String realName;
    @ApiModelProperty(value = "手机号")
    String phone;
    @ApiModelProperty(value = "验证码")
    String code;

}
