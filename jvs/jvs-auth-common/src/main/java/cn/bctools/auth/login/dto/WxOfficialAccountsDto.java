package cn.bctools.auth.login.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * [description]：关注公众号登录
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class WxOfficialAccountsDto {
    /**
     * 二维码 唯一标识 ticket
     */
    private String id;
}
