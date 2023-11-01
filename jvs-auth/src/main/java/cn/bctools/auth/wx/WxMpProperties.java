package cn.bctools.auth.wx;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wechat mp properties
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {
    /**
     * 设置微信公众号的appid
     */
    private String appId;

    /**
     * 设置微信公众号的app secret
     */
    private String secret;

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
        return StrUtil.isEmpty(this.aesKey) || StrUtil.isEmpty(secret) || StrUtil.isEmpty(appId) || StrUtil.isEmpty(token);
    }
}
