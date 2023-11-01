package cn.bctools.auth.service.impl;


import cn.bctools.auth.entity.SysConfig;
import cn.bctools.auth.entity.enums.SysConfigFieldEnum;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.mapper.SysConfigMapper;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.web.utils.WebUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Auto Generator
 */
@Service
@AllArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    public static final String TRUE = "true";


    @Override
    public void saveConfig(String tenantId, String appId, SysConfigTypeEnum type, Map<String, Object> config) {
        // 域名唯一
        String host = parseDomain(config.get(SysConfigFieldEnum.DOMAIN.getValue()));
        SysConfig domainConfig = getDomainConfig(host);
        if (ObjectNull.isNotNull(domainConfig) && (Boolean.FALSE.equals(domainConfig.getJvsTenantId().equals(tenantId))
                || Boolean.FALSE.equals(domainConfig.getAppId().equals(appId)))) {
            throw new BusinessException("域名不能重复");
        }
        if (StringUtils.isNotBlank(host)) {
            config.put(SysConfigFieldEnum.DOMAIN.getValue(), host);
        }

        // 删除旧配置
        remove(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getJvsTenantId, tenantId).eq(SysConfig::getAppId, appId).eq(SysConfig::getType, type));
        // 保存新配置
        List<SysConfig> sysConfigs = config.entrySet().stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getValue()))
                .map(e -> new SysConfig()
                        .setJvsTenantId(tenantId)
                        .setAppId(appId)
                        .setType(type)
                        .setName(e.getKey())
                        .setContent(e.getValue())
                ).collect(Collectors.toList());
        saveBatch(sysConfigs);
    }

    /**
     * 解析域名
     *
     * @param domain
     * @return
     */
    private String parseDomain(Object domain) {
        if (ObjectNull.isNull(domain)) {
            return null;
        }
        String domainStr = String.valueOf(domain);
        URL url = URLUtil.url(domainStr);
        if (StringUtils.isBlank(url.getHost())) {
            return domainStr;
        }
        return url.getHost();
    }

    /**
     * 获取域名配置
     *
     * @param host
     * @return
     */
    private SysConfig getDomainConfig(String host) {
        if (StringUtils.isBlank(host)) {
            return null;
        }
        return getOne(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getName, SysConfigFieldEnum.DOMAIN.getValue()).eq(SysConfig::getContent, host));
    }

    @Override
    public Map<String, Map<String, Object>> getTenantApp(String tenantId) {
        List<SysConfig> configs = list(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getJvsTenantId, tenantId));
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }
        return convertConfig(configs);
    }

    @Override
    public Map<String, Object> getConfig(String appId, SysConfigTypeEnum type) {
        return getConfig(appId, type, Boolean.FALSE);
    }

    @Override
    public Map<String, Object> getConfig(String appId, SysConfigTypeEnum type, Boolean secrecy) {
        if (StringUtils.isBlank(appId)) {
            throw new BusinessException("应用id不能为空");
        }
        String tenantId = TenantContextHolder.getTenantId();
        List<SysConfig> configs = list(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getJvsTenantId, tenantId)
                .eq(SysConfig::getAppId, appId)
                .eq(SysConfig::getType, type));
        if (CollectionUtils.isEmpty(configs)) {
            return Collections.emptyMap();
        }
        Map<String, Object> config = configs.stream().collect(Collectors.toMap(SysConfig::getName, this::convertValue));
        // 拼接重定向地址
        Object redirectUri = config.get(SysConfigFieldEnum.REDIRECT_URI.getValue());
        if (ObjectNull.isNotNull(redirectUri)) {
            HttpServletRequest request = WebUtils.getRequest();
            String url = request.getHeader("Referer");
            URL u = URLUtil.url(url);
            String host = URLUtil.getHost(u).getHost();
            StringBuilder rdUri = new StringBuilder(u.getProtocol()).append("://").append(host);
            if (u.getPort() > 0) {
                rdUri.append(":").append(u.getPort());
            }
            rdUri.append(redirectUri);
            config.put(SysConfigFieldEnum.REDIRECT_URI.getValue(), rdUri.toString());
        }
        // 移除需要保密的字段
        if (secrecy) {
            Arrays.stream(SysConfigFieldEnum.values()).filter(SysConfigFieldEnum::getSecrecy).forEach(c -> config.remove(c.getValue()));
        }
        // 额外字段
        config.put(SysConfigFieldEnum.TENANT_ID.getValue(), configs.get(0).getJvsTenantId());
        return config;
    }

    @Override
    public List<SysConfigTypeEnum> getEnableScanConfigType(String appId) {
        if (StringUtils.isBlank(appId)) {
            throw new BusinessException("应用id不能为空");
        }
        String tenantId = TenantContextHolder.getTenantId();
        Set<SysConfigTypeEnum> enableTypes = Optional.ofNullable(list(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getJvsTenantId, tenantId)
                .eq(SysConfig::getAppId, appId)
                .eq(SysConfig::getName, SysConfigFieldEnum.ENABLE.getValue())
                .eq(SysConfig::getContent, TRUE))).orElse(new ArrayList<>())
                .stream().map(SysConfig::getType).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(enableTypes)) {
            return Collections.emptyList();
        }
        List<SysConfig> configs = list(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getJvsTenantId, tenantId)
                .eq(SysConfig::getAppId, appId)
                .in(SysConfig::getType, enableTypes)
                .eq(SysConfig::getName, SysConfigFieldEnum.ENABLE_SCAN.getValue())
                .eq(SysConfig::getContent, TRUE));
        if (CollectionUtils.isEmpty(configs)) {
            return Collections.emptyList();
        }
        return configs.stream().map(SysConfig::getType).collect(Collectors.toList());
    }

    @Override
    public List<SysConfigTypeEnum> getEnableConfigType(String appId, List<SysConfigTypeEnum> configTypes) {
        if (StringUtils.isBlank(appId)) {
            throw new BusinessException("应用id不能为空");
        }
        String tenantId = TenantContextHolder.getTenantId();
        List<SysConfig> configs = list(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getJvsTenantId, tenantId)
                .eq(SysConfig::getAppId, appId)
                .in(SysConfig::getType, configTypes)
                .eq(SysConfig::getName, SysConfigFieldEnum.ENABLE.getValue())
                .eq(SysConfig::getContent, TRUE));
        if (CollectionUtils.isEmpty(configs)) {
            return Collections.emptyList();
        }
        return configs.stream().map(SysConfig::getType).collect(Collectors.toList());
    }


    /**
     * 配置集合转对象
     *
     * @param configs
     * @return
     */
    private Map<String, Map<String, Object>> convertConfig(List<SysConfig> configs) {
        return configs.stream().collect(Collectors.groupingBy(d -> String.valueOf(d.getAppId()), Collectors.mapping(e -> e, Collectors.toList())))
                .entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> e.getValue().stream().collect(Collectors.groupingBy(SysConfig::getType, Collectors.mapping(d -> d, Collectors.toList())))))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, d -> d.getValue().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getValue(), e -> e.getValue().stream().collect(Collectors.toMap(SysConfig::getName, this::convertValue))))));
    }

    private Object convertValue(SysConfig sysConfig) {
        Object val = sysConfig.getContent();
        if (SysConfigFieldEnum.ENABLE.getValue().equals(sysConfig.getName()) || SysConfigFieldEnum.ENABLE_SCAN.getValue().equals(sysConfig.getName())) {
            val = TRUE.equals(sysConfig.getContent()) ? Boolean.TRUE : Boolean.FALSE;
        }
        return Optional.ofNullable(val).orElse("");
    }

    @Override
    public Map<String, Object> key(String tenantId, String clientId, SysConfigTypeEnum type) {
        // 判断配置是否启用
        SysConfig basicEnableConfig = getOne(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getJvsTenantId, tenantId)
                .eq(SysConfig::getAppId, clientId)
                .eq(SysConfig::getType, type)
                .eq(SysConfig::getName, SysConfigFieldEnum.ENABLE.getValue())
                .eq(SysConfig::getContent, TRUE));
        if (ObjectNull.isNull(basicEnableConfig)) {
            return Collections.emptyMap();
        }
        Map<String, Object> collect = list(Wrappers.query(new SysConfig().setAppId(clientId).setJvsTenantId(tenantId).setType(type)))
                .stream()
                .filter(e -> ObjectNull.isNotNull(e.getContent()))
                .collect(Collectors.toMap(SysConfig::getName, SysConfig::getContent));
        if (collect.isEmpty()) {
            return Collections.emptyMap();
        }
        return collect;
    }

}
