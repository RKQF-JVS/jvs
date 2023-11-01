package cn.bctools.auth.wx;

import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhuXiaoKang
 * @Description: 微信公众号配置
 */

@Slf4j
@Component
@AllArgsConstructor
public class WxMpConfig {
    private static final SysConfigTypeEnum CONFIG_TYPE = SysConfigTypeEnum.WECHAT_MP;
    private final WxMpService wxMpService;

    public void addConfigStorage(String appId) {
        if (StringUtils.isBlank(appId)) {
            throw new BusinessException("应用id不能为空");
        }
        if (refresh(wxMpService)) {
           wxMpService.addConfigStorage(appId, buildWxMpConfigStorage());
        }
    }

    /**
     * 是否刷新配置
     * @param service
     * @return TRUE-刷新，FALSE-不刷新
     */
    private Boolean refresh(WxMpService service) {
        WxMpConfigStorage config = service.getWxMpConfigStorage();
        // TRUE- 配置无变化， FALSE-配置已变更
        boolean different = ObjectNull.isNotNull(config)
                && AuthConfigUtil.appId(CONFIG_TYPE).equals(config.getAppId())
                && AuthConfigUtil.secret(CONFIG_TYPE).equals(config.getSecret())
                && AuthConfigUtil.aesKey(CONFIG_TYPE).equals(config.getAesKey())
                && AuthConfigUtil.token(CONFIG_TYPE).equals(config.getToken());
        return different ? Boolean.FALSE : Boolean.TRUE;
    }


    private WxMpDefaultConfigImpl buildWxMpConfigStorage() {
        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        wxMpConfigStorage.setAppId(AuthConfigUtil.appId(CONFIG_TYPE));
        wxMpConfigStorage.setAesKey(AuthConfigUtil.aesKey(CONFIG_TYPE));
        wxMpConfigStorage.setSecret(AuthConfigUtil.secret(CONFIG_TYPE));
        wxMpConfigStorage.setToken(AuthConfigUtil.token(CONFIG_TYPE));
        return wxMpConfigStorage;
    }
}
