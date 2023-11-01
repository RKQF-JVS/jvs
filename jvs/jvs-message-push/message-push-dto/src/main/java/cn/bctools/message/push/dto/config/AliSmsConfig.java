package cn.bctools.message.push.dto.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("阿里短信服务配置项")
@Component
@ConfigurationProperties(prefix = "sms")
public class AliSmsConfig extends BaseConfig{
    private static final long serialVersionUID = -8917561483702017826L;

    @ApiModelProperty("短信签名")
    private String signature;

    @ApiModelProperty("AccessKey ID")
    private String accessKeyId;

    @ApiModelProperty("AccessKey Secret")
    private String accessKeySecret;

    /**
     * 如产品 ECS 的某个 Endpoint：ecs.cn-hangzhou.aliyuncs.com
     * 每个产品都有其独立的 Endpoint，并且 Endpoint 与服务区域 RegionId 有关，不同地域可能是不同的 Endpoint
     * 在网址 https://help.aliyun.com/document_detail/419270.html 中进行查看
     */
    @ApiModelProperty("请求接口服务的网络域名")
    private String endpoint = "dysmsapi.aliyuncs.com";

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    public Boolean isEmpty() {
        return StrUtil.isEmpty(this.signature) || StrUtil.isEmpty(this.accessKeyId) || StrUtil.isEmpty(accessKeySecret) || StrUtil.isEmpty(endpoint);
    }
}
