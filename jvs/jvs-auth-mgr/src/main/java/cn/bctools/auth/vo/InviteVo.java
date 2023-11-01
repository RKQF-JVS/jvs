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

    @ApiModelProperty("邀请码")
    String code;

    @ApiModelProperty("是否需要审核")
    Boolean status;

    @ApiModelProperty("邀请内容")
    String content;
    @ApiModelProperty("租戶ID")
    String tenantId;

}
