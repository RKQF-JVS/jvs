package cn.bctools.auth.service;

import cn.bctools.auth.component.OtherAuthComponent;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.component.UserInfoComponent;
import cn.bctools.auth.constants.AuthConstant;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.utils.IpUtil;
import cn.bctools.common.entity.dto.TenantsDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.log.utils.IpUtils;
import cn.bctools.oauth2.dto.CustomUser;
import cn.bctools.oauth2.utils.WebUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 此操作直接查库
 *
 * @author guojing
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
        Map<String, UserTenant> listMap = userTenantService.list(Wrappers.query(new UserTenant().setUserId(info.getId())))
                .stream()
                //租户还存在，不能被删除或禁用
                .filter(e -> {
                    TenantPo byId = tenantService.getById(e.getTenantId());
                    if (ObjectNull.isNull(byId)) {
                        return false;
                    } else {
                        if (byId.getEnable()) {
                            tenants.add(new TenantsDto(byId.getId(), byId.getShortName(), byId.getIcon()));
                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .collect(Collectors.toMap(UserTenant::getTenantId, Function.identity()));

        Set<String> tenantIds = listMap.keySet();
        // 设置登录客户端信息
        String appId = setClientInfo(userDto, request);

        if (ObjectUtil.isNotEmpty(tenantIds)) {
            // 获取租户id
            String tenantId = getTenantId(request, tenantIds);
            //判断是否是超级管理员
            //存在多个租户
            if (tenantIds.contains(tenantId)) {
                userDto.setTenantId(tenantId);
                UserTenant userTenant = listMap.get(tenantId);
                TenantsDto copy = BeanCopyUtil.copy(userTenant, TenantsDto.class);
                //将扩展部分存放进去
                BeanCopyUtil.copy(userTenant, userDto);
                userDto.setTenant(copy);
                String adminUserId = tenantService.getById(tenantId).getAdminUserId();
                //判断是否是超级管理员
                userDto.setAdminFlag(adminUserId.equals(info.getId()));
            }
            userDto.setTenants(tenants);
        }

        log.debug("登录的用户信息：{}", userDto);
        userDto.setId(info.getId());
        set(userDto, request);

        //如果没有选租户，则直接不查询数据权限和租户权限.直接返回
        UserInfoDto<UserDto> data;
        if (ObjectUtil.isEmpty(userDto.getTenantId())) {
            data = new UserInfoDto<>();
        } else {
            data = userInfoComponent.getUserInfoDto(appId, userDto.getId(), userDto.getAdminFlag(), userDto.getTenantId());
        }
        data.setUserDto(userDto);

        return BeanUtil.copyProperties(data, CustomUser.class);
    }

    public void set(UserDto userDto, HttpServletRequest request) {
        String userAgent = request.getHeader("User-agent");
        userDto.setUserAgent(userAgent);
        String ipAddr = IpUtil.getIpAddr(request);
        //转换IP地址
        String transition = IpUtils.transition(ipAddr);
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
    private String setClientInfo(UserDto userDto, HttpServletRequest request) {
        UserDto user = getRefreshTokenOldUser(request);
        String clientId = StringUtils.isNotBlank(user.getClientId()) ? user.getClientId() : request.getParameter("client_id");
        // 三方应用clientId
        String appClientId = request.getParameter(AuthConstant.APP_CLIENT_ID);
        // 三方应用回调地址
        String callBackUrl = request.getParameter(AuthConstant.CALL_BACK_URL);
        if (StringUtils.isNotEmpty(appClientId)) {
            clientId = appClientId;
        }
        // 若回调地址为空、应用id为空，从已登录用户信息中获取
        if (StringUtils.isBlank(callBackUrl)) {
            callBackUrl = user.getCallBackUrl();
        }

        Apply apply = Optional.ofNullable(applyService.getOne(Wrappers.lambdaQuery(new Apply().setAppKey(clientId)).select(Apply::getName, Apply::getId, Apply::getAppKey, Apply::getRegisteredRedirectUris)))
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
        return apply.getId();
    }

    /**
     * 获取租户id
     *
     * @param request
     * @param tenantIds
     * @return
     */
    private String getTenantId(HttpServletRequest request, Set<String> tenantIds) {
        //获取选择的租户
        String tenantId = request.getParameter("switch");
        if (tenantIds.size() == 1) {
            //只有一个租户，直接返回，不用请求两次,选择租户直接等于当前租户
            tenantId = tenantIds.iterator().next();
        }

        // 是刷新token，且未指定租户，则从上次刷新token的OAuth2Authentication中获取租户
        if (StringUtils.isBlank(tenantId)) {
            tenantId = getRefreshTokenOldUser(request).getTenantId();
        }

        // 不止一个租户，且未选择租户，则根据域名匹配
        if (StringUtils.isBlank(tenantId)) {
            String host = request.getHeader("Referer");
            tenantId = tenantService.getTenantIdFromHost(host, tenantIds).map(TenantPo::getId).orElse(tenantId);
        }
        return tenantId;
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
        if (StringUtils.isNotBlank(refreshTokenValue)) {
            OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
            OAuth2Authentication authentication = this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
            UserInfoDto userInfoDto = (UserInfoDto) authentication.getPrincipal();
            currentUserDto = userInfoDto.getUserDto();
        }
        return Optional.ofNullable(currentUserDto).orElse(new UserDto());
    }

}
