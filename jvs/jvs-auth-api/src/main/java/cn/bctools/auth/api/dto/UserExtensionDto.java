package cn.bctools.auth.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel("用户组对象")
public class UserExtensionDto {
    private String id;
    private String type;
    private String openId;
    private String userId;
    private String nickname;
    private Map<String, Object> extension;
    private Map<String, Object> remark;
}
