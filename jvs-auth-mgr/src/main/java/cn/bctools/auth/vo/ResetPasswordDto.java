package cn.bctools.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author auto
 */
@Data
@ApiModel("设置密码")
public class ResetPasswordDto {

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", notes = "加密传输", required = true)
    private String password;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "重置密码", notes = "加密传输", required = true)
    private String rePassword;


}
