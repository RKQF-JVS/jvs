package cn.bctools.message.push.dto.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 微信公众号配置
 *
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@ApiModel("微信公众号配置")
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "wx.mp")
public class WechatOfficialAccountConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = -9206902816158196669L;

    /**
     * 设置微信公众号的appid
     */
    private String appId;
    /**
     * 设置微信公众号的app secret
     */
    private String appSecret;
    /**
     * 设置微信公众号的token
     */
    private String token;
    /**
     * 设置微信公众号的EncodingAESKey
     */
    private String aesKey;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    public Boolean isEmpty() {
        return StrUtil.isEmpty(this.aesKey) || StrUtil.isEmpty(appSecret) || StrUtil.isEmpty(appId) || StrUtil.isEmpty(token);
    }

}
