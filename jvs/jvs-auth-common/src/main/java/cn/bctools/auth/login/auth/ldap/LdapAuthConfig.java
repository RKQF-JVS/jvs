package cn.bctools.auth.login.auth.ldap;

import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: ZhuXiaoKang
 * @Description: ldap配置
 */
@Component
@AllArgsConstructor
public class LdapAuthConfig {

    public static final SysConfigTypeEnum CONFIG_TYPE = SysConfigTypeEnum.LDAP;
    private static final String URL_SEPARATOR = ";";
    private final Environment environment;
    private final ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy;
    private static final ConcurrentHashMap<String, LdapTemplate> ldapCache = new ConcurrentHashMap<>();

    /**
     * 获取ldapTemplate
     * @param appId 系统应用id
     * @return
     */
    public LdapTemplate ldapTemplate(String appId) {
        LdapTemplate ldapTemplate = ldapCache.get(appId);
        if (ObjectNull.isNull(ldapTemplate)) {
             ldapTemplate = buildLdapTemplate();
             ldapCache.put(appId, ldapTemplate);
        }
        // 配置刷新则重新创建ldapTemplate
        if (refresh(ldapTemplate)) {
            ldapTemplate = buildLdapTemplate();
            ldapCache.put(appId, ldapTemplate);
        }
        return ldapTemplate;
    }

    /**
     * 是否刷新配置
     * @return TRUE-刷新，FALSE-不刷新
     */
    private Boolean refresh(LdapTemplate ldapTemplate) {
        LdapContextSource ldapContextSource = (LdapContextSource)ldapTemplate.getContextSource();
        // TRUE- 配置无变化， FALSE-配置已变更
        String urls = Arrays.stream(ldapContextSource.getUrls()).collect(Collectors.joining(";"));
        boolean different = AuthConfigUtil.base(CONFIG_TYPE).equals(ldapContextSource.getBaseLdapPathAsString())
                && AuthConfigUtil.urls(CONFIG_TYPE).equals(urls)
                && AuthConfigUtil.username(CONFIG_TYPE).equals(ldapContextSource.getUserDn())
                && AuthConfigUtil.password(CONFIG_TYPE).equals(ldapContextSource.getPassword());
        return different ? Boolean.FALSE : Boolean.TRUE;
    }

    private LdapTemplate buildLdapTemplate() {
        LdapProperties properties = properties();
        ContextSource contextSource = ldapContextSource(properties);
        LdapProperties.Template template = properties.getTemplate();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        propertyMapper.from(template.isIgnorePartialResultException())
                .to(ldapTemplate::setIgnorePartialResultException);
        propertyMapper.from(template.isIgnoreNameNotFoundException()).to(ldapTemplate::setIgnoreNameNotFoundException);
        propertyMapper.from(template.isIgnoreSizeLimitExceededException())
                .to(ldapTemplate::setIgnoreSizeLimitExceededException);
        return ldapTemplate;
    }

    private LdapContextSource ldapContextSource(LdapProperties properties) {
        LdapContextSource source = new LdapContextSource();
        dirContextAuthenticationStrategy.ifUnique(source::setAuthenticationStrategy);
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        propertyMapper.from(properties.getUsername()).to(source::setUserDn);
        propertyMapper.from(properties.getPassword()).to(source::setPassword);
        propertyMapper.from(properties.getAnonymousReadOnly()).to(source::setAnonymousReadOnly);
        propertyMapper.from(properties.getBase()).to(source::setBase);
        propertyMapper.from(properties.determineUrls(environment)).to(source::setUrls);
        propertyMapper.from(properties.getBaseEnvironment()).to(
                (baseEnvironment) -> source.setBaseEnvironmentProperties(Collections.unmodifiableMap(baseEnvironment)));
        source.afterPropertiesSet();
        return source;
    }

    private LdapProperties properties() {
        String base = AuthConfigUtil.base(CONFIG_TYPE);
        String urls = AuthConfigUtil.urls(CONFIG_TYPE);
        String userName = AuthConfigUtil.username(CONFIG_TYPE);
        String password = AuthConfigUtil.password(CONFIG_TYPE);
        boolean check = StringUtils.isBlank(base) || StringUtils.isBlank(urls)
                || StringUtils.isBlank(userName) || StringUtils.isBlank(password);
        if (check) {
            throw new BusinessException("请完善LDAP配置");
        }

        LdapProperties properties = new LdapProperties();
        properties.setBase(base);
        properties.setUrls(urls.replace(" ", "").split(URL_SEPARATOR));
        properties.setUsername(userName);
        properties.setPassword(password);
        return properties;
    }


    public String accountAttribute() {
        return AuthConfigUtil.accountAttribute(CONFIG_TYPE);
    }

    public String base() {
        return AuthConfigUtil.base(CONFIG_TYPE);
    }

}
