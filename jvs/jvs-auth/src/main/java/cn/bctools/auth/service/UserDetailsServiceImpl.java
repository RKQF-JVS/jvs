package cn.bctools.auth.service;

import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.component.OtherAuthComponent;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.component.UserInfoComponent;
import cn.bctools.auth.constants.AuthConstant;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.common.entity.dto.TenantsDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.*;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.log.utils.IpUtils;
import cn.bctools.oauth2.dto.CustomUser;
import cn.bctools.redis.utils.RedisUtils;
import cn.bctools.web.utils.IpUtil;
import cn.bctools.web.utils.WebUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 此操作直接查库
 *
 * @author 
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    SmsEmailComponent smsComponent;
    UserService userService;
    UserInfoComponent userInfoComponent;
    TenantService tenantService;
    UserExtensionService userExtensionService;
    UserTenantService userTenantService;
    ApplyService applyService;
    OtherAuthComponent justAuthComponent;
    TokenStore tokenStore;
    RoleService roleService;
    UserRoleService userRoleService;
    RedisUtils redisUtils;
    SysConfigService sysConfigService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        try {
            User info = userService.info(username);
            return getUserDetails(info, "帐号密码");
        } catch (Exception e) {
            log.error("用户登录失败", e);
            throw new BadCredentialsException(e.getMessage());
        }
    }


    /**
     * 标识使用@分割  如phone@13594163317_0000
     * wx@3049
     *
     * @param appId
     * @param identification
     * @return
     */
    public UserDetails loadUserByOtherAuth(String identification, String appId) {
        try {
            String[] split = identification.split(StringPool.AT);
            String loginType = split[0];
            return getUserDetails(loginType, split[1], appId);
        } catch (Exception e) {
            log.error("用户登录失败，{},{}", identification, appId, e);
            throw new BadCredentialsException(e.getMessage());
        }
    }

    /**
     * 独立出来，避免数据不存在情况无法回滚
     *
     * @param loginType
     * @param s
     * @param appId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    UserDetails getUserDetails(String loginType, String s, String appId) {
        User user = justAuthComponent.getUser(loginType, s, appId);
        return getUserDetails(user, loginType);
    }

    /**
     * 根据当前用户选择登录的用户
     *
     * @param info
     * @param loginType
     * @return
     */
    private UserDetails getUserDetails(User info, String loginType) {
        HttpServletRequest request = WebUtils.getRequest();
        //清楚请求多租户信息
        TenantContextHolder.clear();
        UserDto userDto = BeanCopyUtil.copy(info, UserDto.class);
        if (ObjectNull.isNull(userDto.getAccountName())) {
            userDto.setAccountName(info.getPhone());
        }
        //查询用户扩展信息
        Map<String, Object> collect = userExtensionService.list(Wrappers.query(new UserExtension().setUserId(info.getId()))).stream().collect(Collectors.toMap(UserExtension::getType, UserExtension::getExtension));
        userDto.setExceptions(collect);
        userDto.setLoginType(loginType);

        List<TenantsDto> tenants = new ArrayList<>();

        //获取当前用户在哪些租户下
        Map<String, UserTenant> listMap = userTenantService.list(Wrappers.query(new UserTenant().setCancelFlag(false).setUserId(info.getId())))
                .stream()
                //租户还存在，不能被删除或禁用
                .peek(e -> {
                            try {
                                //刷新token，或登录的时候的时候更新租户数据数据信息,手机号不能为空
                                if (ObjectNull.isNotNull(info.getPhone())) {
                                    if (!info.getPhone().equals(e.getPhone()) || !info.getRealName().equals(e.getRealName())) {
                                        e.setPhone(info.getPhone());
                                        e.setRealName(info.getRealName());
                                        userTenantService.updateById(e);
                                    }
                                }
                            } catch (Exception exception) {
                                log.error("用户信息发生变更,更新数据失败，{}", JSONObject.toJSONString(e));
                            }
                        }
                )
                .filter(e -> {
                    TenantPo byId = tenantService.getById(e.getTenantId());
                    if (ObjectNull.isNull(byId)) {
                        return false;
                    } else {
                        if (byId.getEnable()) {
                            tenants.add(new TenantsDto(byId.getId(), byId.getShortName(), byId.getIcon(), byId.getLogo(), byId.getName()));
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .collect(Collectors.toMap(UserTenant::getTenantId, Function.identity()));

        Set<String> tenantIds = listMap.keySet();
        // 设置登录客户端信息
        Apply apply = setClientInfo(userDto, request);
        String appId = apply.getId();

        if (ObjectUtil.isNotEmpty(tenantIds)) {
            // 获取租户id
            setTenantId(request, tenantIds, userDto, listMap, info.getId(), tenants, apply);
            userDto.setTenants(tenants);
        }

        // 判断当前用户是否为超级管理员
        List<String> currentRoleIds = userRoleService.list(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, info.getId()))
                .stream().map(UserRole::getRoleId)
                .collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(currentRoleIds)) {
            List<String> roleType = roleService.list(new LambdaQueryWrapper<Role>().in(Role::getId, currentRoleIds))
                    .stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            userDto.setRoleType(roleType);
        }

        log.debug("登录的用户信息：{}", userDto);
        userDto.setId(info.getId());
        setIp(userDto, request);

        //如果没有选租户，则直接不查询数据权限和租户权限.直接返回
        UserInfoDto<UserDto> dataInfo;
        String tenantId = userDto.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            dataInfo = new UserInfoDto<>();
        } else {
            dataInfo = userInfoComponent.getUserInfoDto(appId, tenantId, userDto);
        }
        dataInfo.setUserDto(userDto);
        return BeanUtil.copyProperties(dataInfo, CustomUser.class);
    }

    public void setIp(UserDto userDto, HttpServletRequest request) {
        String userAgent = request.getHeader("User-agent");
        userDto.setUserAgent(userAgent);
        String ipAddr = IpUtil.getIpAddr(request);
        //转换IP地址
        String transition = IpUtils.transition(ipAddr, userDto.getClientId());
        userDto.setIp(transition);
    }

    /**
     * 设置登录客户端信息
     * <p>
     * 三方应用登录流程：
     * 1) 三方应用 > 统一登录页 : 需要传递app_client_id和call_back_url
     * 2) 统一登录页 > auth : 登录成功，返回用户信息（包含回调地址）
     * 3) 统一登录页 > 根据call_back_url重定向
     *
     * @param userDto
     * @param request
     * @return
     */
    private Apply setClientInfo(UserDto userDto, HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        // 三方应用clientId
        String appClientId = request.getParameter(AuthConstant.APP_CLIENT_ID);
        // 三方应用回调地址
        String callBackUrl = request.getParameter(AuthConstant.CALL_BACK_URL);
        if (StringUtils.isNotEmpty(appClientId)) {
            clientId = appClientId;
        }
        // 若回调地址为空、应用id为空，从已登录用户信息中获取
        if (StringUtils.isBlank(callBackUrl)) {
//            callBackUrl = user.getCallBackUrl();
        }
        //防治结核病要启用
        Apply apply = Optional.ofNullable(applyService.getOne(Wrappers.lambdaQuery(new Apply().setEnable(true).setAppKey(clientId))))
                .orElseThrow(() -> new BusinessException("应用未注册"));
        if (StringUtils.isNotEmpty(callBackUrl)) {
            callBackUrl = URLUtil.decode(callBackUrl);
            if (CollectionUtils.isEmpty(apply.getRegisteredRedirectUris()) || !apply.getRegisteredRedirectUris().contains(callBackUrl)) {
                throw new BusinessException("登录失败,回调地址未注册");
            }
            userDto.setCallBackUrl(callBackUrl);
        }

        userDto.setClientId(apply.getAppKey());
        userDto.setClientName(apply.getName());
        return apply;
    }

    /**
     * 获取租户id
     *
     * @param request
     * @param tenantIds
     * @param userDto
     * @param listMap
     * @param tenants
     * @param apply
     * @return
     */
    private UserDto setTenantId(HttpServletRequest request, Set<String> tenantIds, UserDto userDto, Map<String, UserTenant> listMap, String userId, List<TenantsDto> tenants, Apply apply) {
        //获取选择的租户
        String tenantId = null;
        if (tenantIds.size() == 1) {
            //只有一个租户，直接返回，不用请求两次,选择租户直接等于当前租户
            tenantId = tenantIds.iterator().next();
        }
        UserDto refreshTokenOldUser = getRefreshTokenOldUser(request);

        // 是刷新token，且未指定租户，则从上次刷新token的OAuth2Authentication中获取租户
        if (StringUtils.isBlank(tenantId)) {
            tenantId = refreshTokenOldUser.getTenantId();
        }

        String key = "login:last:tenantId:" + userId;
        // 不止一个租户，且未选择租户，则根据域名匹配
        if (StringUtils.isBlank(tenantId)) {
            String host = URLUtil.getHost(URLUtil.url(request.getHeader("Referer"))).getHost();
            tenantId = tenantService.getTenantIdFromHost(host, tenantIds).map(TenantPo::getId).orElse(tenantId);
            if (StringUtils.isBlank(tenantId)) {
                //上一次租户登录为准，或第一个
                if (redisUtils.hasKey(key)) {
                    tenantId = redisUtils.get(key).toString();
                } else {
                    tenantId = tenants.get(0).getId();
                }
            }
        }
        redisUtils.set(key, tenantId);
        //设置这一次登录的租户ID
        tenants.forEach(e -> e.setLogo(apply.getLogo()).setIcon(apply.getIcon()));
        //判断是否是超级管理员
        //存在多个租户
        if (tenantIds.contains(tenantId)) {
            userDto.setTenantId(tenantId);
            UserTenant userTenant = listMap.get(tenantId);
            //将扩展部分存放进去
            BeanCopyUtil.copy(userTenant, userDto);
            for (TenantsDto tenant : tenants) {
                //暂时不考虑多租户显示
                tenant.setLogo(apply.getLogo());
                tenant.setIcon(apply.getIcon());
                if (tenant.getId().equals(tenantId)) {
                    tenant.setDeptId(userTenant.getDeptId());
                    tenant.setDeptName(userTenant.getDeptName());
                    tenant.setEmployeeNo(userTenant.getEmployeeNo());
                    tenant.setJobId(userTenant.getJobId());
                    tenant.setJobName(userTenant.getJobName());
                    tenant.setLevel(userTenant.getLevel());
                    //切换租户ID
                    String oldTenantId = TenantContextHolder.getTenantId();
                    TenantContextHolder.setTenantId(tenantId);
                    Map<String, Object> config = sysConfigService.getConfig(apply.getAppKey(), SysConfigTypeEnum.BASIC);
                    TenantContextHolder.setTenantId(oldTenantId);
                    if (MapUtils.isNotEmpty(config)) {
                        Object logo = config.get("logo");
                        if (ObjectNull.isNotNull(logo)) {
                            tenant.setLogo(String.valueOf(logo));
                        }
                        Object icon = config.get("icon");
                        if (ObjectNull.isNotNull(icon)) {
                            tenant.setIcon(String.valueOf(icon));
                        }
                        Object name = config.get("name");
                        if (ObjectNull.isNotNull(name)) {
                            tenant.setName(String.valueOf(name));
                        }
                    }
                    userDto.setTenant(tenant);
                }
            }
            TenantPo byId = tenantService.getById(tenantId);
            String adminUserId = byId.getAdminUserId();
            //判断是否是超级管理员
            userDto.setAdminFlag(adminUserId.equals(userId));
            boolean isAdmin = userRoleService.getRoleByUserId(userId)
                    .stream()
                    .filter(e -> ObjectNull.isNotNull(e.getRoleName()))
                    .anyMatch(e -> e.getRoleName().equals(SysRoleEnum.APP_ADMIN.getName()));
            userDto.setAppAdmin(isAdmin);
            //如果上级为空，并且用户ID和管理员ID一致。则为平台级超级管理员
            userDto.setPlatformAdmin(adminUserId.equals(userId) && ObjectNull.isNull(byId.getParentId()));
        }
        return refreshTokenOldUser;
    }

    /**
     * 若是刷新token请求，则获取刷新token之前的用户信息
     *
     * @param request
     * @return
     */
    private UserDto getRefreshTokenOldUser(HttpServletRequest request) {
        UserDto currentUserDto = null;
        // 是刷新token
        String refreshTokenValue = request.getParameter("refresh_token");
        String aSwitch = request.getParameter("switch");

        if (StringUtils.isNotBlank(refreshTokenValue)) {
            OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
            OAuth2Authentication authentication = this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
            if (ObjectNull.isNotNull(aSwitch)) {
                userInfoDto.getUserDto().setTenantId(aSwitch);
            }
            currentUserDto = userInfoDto.getUserDto();
        }
        return Optional.ofNullable(currentUserDto).orElse(new UserDto().setTenantId(aSwitch));
    }

}
