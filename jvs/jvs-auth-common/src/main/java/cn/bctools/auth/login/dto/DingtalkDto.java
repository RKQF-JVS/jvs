package cn.bctools.auth.login.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DingtalkDto {

    /**
     * 企业内应用唯一标识
     */
    private String agentId;

    /**
     * 免登授权码
     */
    private String code;
}
