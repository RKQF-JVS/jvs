package cn.bctools.auth.login.auth.ldap;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.dto.LdapDto;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.enums.OtherLoginTypeEnum;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.PasswordUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LookupAttemptingCallback;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import java.util.*;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@Slf4j
@AllArgsConstructor
@Component("LDAP")
public class LdapLoginHandler implements LoginHandler<LdapDto> {
    /**
     * LDAP默认登录账号字段
     */
    private static final String DEFAULT_ACCOUNT_ATTRIBUTE = "uid";
    private static final String DISPLAY_NAME = "displayName";

    public static final String LOGIN_TYPE = OtherLoginTypeEnum.LDAP.name();
    /**
     * 不保存到数据库的LDAP属性字段
     */
    private static final List<String> IGNORE_ATTRIBUTE = Arrays.asList("userPassword");
    private final LdapAuthConfig ldapAuthConfig;
    UserService userService;
    UserRoleService userRoleService;
    UserExtensionService userExtensionService;

    @Override
    public User handle(String code, String appId, LdapDto ldapDto) {
        // 鉴权
        DirContextOperations dirContextOperations = authenticate(appId, ldapDto);
        // 返回用户信息
        return getUserInfo(ldapDto.getAccount(), dirContextOperations);
    }

    /**
     *  鉴权
     * @param appId
     * @param ldapDto
     * @return
     */
    private DirContextOperations authenticate(String appId, LdapDto ldapDto) {
        try {
            String accountAttribute = ldapAuthConfig.accountAttribute();
            if (StringUtils.isBlank(accountAttribute)) {
                accountAttribute = DEFAULT_ACCOUNT_ATTRIBUTE;
            }
            EqualsFilter filter = new EqualsFilter(accountAttribute, ldapDto.getAccount());
            LdapQuery ldapQuery = LdapQueryBuilder.query().filter(filter);
            return ldapAuthConfig.ldapTemplate(appId).authenticate(ldapQuery, ldapDto.getPassword(), new LookupAttemptingCallback());
        } catch (Exception e) {
            log.error("登录失败。exception: {}", e.getMessage());
            if (e.getMessage().contains("Invalid Credentials") || e instanceof EmptyResultDataAccessException) {
                throw new BusinessException("用户不存在或密码错误");
            }
            if (e instanceof CommunicationException) {
                throw new BusinessException("无法访问LDAP服务");
            }
            throw new BusinessException("请检查LDAP配置");
        }
    }

    /**
     * 获取用户信息
     * @param account LDAP登录账号
     * @param dirContextOperations LDAP返回数据
     * @return 用户信息
     */
    private User getUserInfo(String account, DirContextOperations dirContextOperations) {
        // 获取用户信息
        String uniqueCode = getUniqueCode(dirContextOperations);
        String openId = getOpenId(uniqueCode);
        UserExtension extension = userExtensionService.getOne(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getType, LOGIN_TYPE)
                .eq(UserExtension::getOpenId, openId));
        if (ObjectUtil.isNotEmpty(extension)) {
            return userService.getById(extension.getUserId());
        }
        // 注册用户
        return registerUser(account, openId, dirContextOperations);
    }

    /**
     * 注册用户
     * @param account LDAP登录账号
     * @param openId 唯一编码
     * @param dirContextOperations LDAP返回数据
     */
    private User registerUser(String account, String openId, DirContextOperations dirContextOperations) {
        Map<String, Object> ldapUser = getLdapAttributes(dirContextOperations);
        String realName = getLdapUserName(openId, ldapUser);
        // 注册
        User user = new User().setRealName(realName).setAccountName(account);
        UserTenant userTenant = new UserTenant().setRealName(realName);
        userService.saveUser(user, userTenant);
        UserExtension userExtension = new UserExtension()
                .setExtension(ldapUser)
                .setOpenId(openId)
                .setNickname(realName)
                .setType(LOGIN_TYPE)
                .setUserId(user.getId());
        //设置为前端用户
        user.setUserType(UserTypeEnum.OTHER_USER);
        userExtensionService.save(userExtension);
        // 默认为游客角色
        userRoleService.grandDefaultSysRole(user.getId());
        return user;
    }

    /**
     * 获取LDAP用户属性
     * @param dirContextOperations LDAP返回数据
     * @return LDAP用户属性属性
     */
    private Map<String, Object> getLdapAttributes(DirContextOperations dirContextOperations) {
        Map<String, Object> attributeMap = new LinkedHashMap<>();
        try {
            NamingEnumeration i = dirContextOperations.getAttributes().getAll();
            while(i.hasMore()) {
                Attribute attribute = (Attribute)i.next();
                if (IGNORE_ATTRIBUTE.contains(attribute.getID())) {
                    continue;
                }
                if (attribute.size() == 1) {
                    attributeMap.put(attribute.getID(), attribute.get());
                } else {
                    int j = 0;
                    for(Iterator var5 = ((Iterable)attribute).iterator(); var5.hasNext(); ++j) {
                        Object value = var5.next();
                        attributeMap.put(attribute.getID() + "[" + j + "]", value);
                    }
                }
            }
        } catch (NamingException var7) {
           log.error("获取LDAP属性失败. exception：{}", var7.getMessage());
           throw new BusinessException("获取LDAP用户属性失败");
        }
        return attributeMap;
    }

    @Override
    public void bind(User user, String code, String appId) {
        String decodedPassword = PasswordUtil.decodedPassword(code, appId);
        LdapDto dto = JSONObject.parseObject(decodedPassword, LdapDto.class);
        DirContextOperations dirContextOperations = authenticate(appId, dto);
        Map<String, Object> authUser = getLdapAttributes(dirContextOperations);
        log.info("[bind] 获取LDAP用户信息: {}", JSONUtil.toJsonStr(authUser));
        String uniqueCode = getUniqueCode(dirContextOperations);
        String openId = getOpenId(uniqueCode);
        String nickname = getLdapUserName(openId, authUser);
        UserExtension extension = userExtensionService.getOne(Wrappers.query(new UserExtension().setType(LOGIN_TYPE).setOpenId(openId)));
        // 判断是否重复绑定
        if (ObjectUtil.isNotEmpty(extension)) {
            throw new BusinessException("该LDAP账户已绑定其它帐号");
        }
        // 绑定用户关键信息
        extension = new UserExtension()
                .setOpenId(openId)
                .setNickname(nickname)
                .setUserId(user.getId())
                .setType(LOGIN_TYPE)
                .setExtension(JSONObject.parseObject(JSONObject.toJSONString(authUser)));
        userService.updateById(user);
        userExtensionService.save(extension);
    }

    private String getUniqueCode(DirContextOperations dirContextOperations) {
        String uniqueCode = dirContextOperations.getStringAttribute(DEFAULT_ACCOUNT_ATTRIBUTE);
        if (StringUtils.isBlank(uniqueCode)) {
            log.error("获取LDAP用户的唯一编码失败");
            throw new BusinessException("获取LDAP唯一编码失败");
        }
        return uniqueCode;
    }

    private String getOpenId(String uniqueCode) {
        return ldapAuthConfig.base() + ",account=" + uniqueCode;
    }

    private String getLdapUserName(String openId, Map<String, Object> ldapUser) {
        return ObjectNull.isNull(ldapUser.get(DISPLAY_NAME)) ? openId : String.valueOf(ldapUser.get(DISPLAY_NAME));
    }
}
