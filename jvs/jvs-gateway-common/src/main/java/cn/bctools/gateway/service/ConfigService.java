package cn.bctools.gateway.service;

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.gateway.entity.Config;
import cn.bctools.gateway.mapper.ConfigMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigService {

    ConfigMapper configMapper;

    /**
     * 根据域名获取租户
     *
     * @param host
     * @return
     */
    public String fromHost(String host) {
        if (ObjectNull.isNotNull(host)) {
//            URL u = URLUtil.url(host);
//            String domain = URLUtil.getHost(u).getHost();
            Config config = configMapper.selectOne(Wrappers.query(new Config().setType("BASIC").setName("domain").setContent(host)));
            if (ObjectNull.isNotNull(config)) {
                Config enableConfig = configMapper.selectOne(Wrappers.query(new Config()
                        .setJvsTenantId(config.getJvsTenantId())
                        .setAppId(config.getAppId())
                        .setType("BASIC")
                        .setName("enable")
                        .setContent("true")));
                if (ObjectNull.isNotNull(enableConfig)) {
                    return config.getJvsTenantId();
                }
            }
        }
        //返回默认主租户为1
        return "1";
    }
}
