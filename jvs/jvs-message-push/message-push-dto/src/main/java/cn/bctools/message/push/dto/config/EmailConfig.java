package cn.bctools.message.push.dto.config;

import cn.bctools.common.utils.ObjectNull;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 邮件配置
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel("邮件配置")
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = 3833630267273040696L;

    @ApiModelProperty("地址")
    private String host;
    @ApiModelProperty("加密端口号")
    private int port = 465;
    @ApiModelProperty("发送地址")
    private String from;
    @ApiModelProperty("发送密码")
    private String pass;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    public Boolean isEmpty() {
        return StrUtil.isEmpty(this.host) || ObjectNull.isNull(port) || StrUtil.isEmpty(from) ||  StrUtil.isEmpty(pass);
    }


}
