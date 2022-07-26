package cn.bctools.auth.login.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class InviteDto {

    /**
     * 租户ID
     */
    String id;
    /**
     * 姓名
     */
    String realName;
    /**
     * 手机号
     */
    String phone;
    /**
     * 邀请码为
     */
    String invite;


}
