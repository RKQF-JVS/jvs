package cn.bctools.auth.login.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@ApiModel("手机号登录")
public class PhoneDto {
    /**
     * 手机号
     */
    String phone;
    /**
     * 验证码
     */
    String code;

}
