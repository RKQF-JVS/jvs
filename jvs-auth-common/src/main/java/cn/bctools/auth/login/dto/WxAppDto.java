package cn.bctools.auth.login.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
public class WxAppDto {

    String encryptedData;

    String ivStr;

    String code;

}
