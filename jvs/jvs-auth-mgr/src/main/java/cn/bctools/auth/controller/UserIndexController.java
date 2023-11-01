package cn.bctools.auth.controller;

import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.component.PermissionComponent;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.component.UserInfoComponent;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.entity.enums.BulletinTypeEnum;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.auth.ding.DdBase;
import cn.bctools.auth.login.auth.ding.DdScanLoginHandler;
import cn.bctools.auth.login.auth.ldap.LdapLoginHandler;
import cn.bctools.auth.login.auth.wx.WxAppLoginHandler;
import cn.bctools.auth.login.auth.wx.WxLoginHandler;
import cn.bctools.auth.login.auth.wx.WxOfficialAccountsLoginHandler;
import cn.bctools.auth.login.auth.wx.enterprise.WxEnterpriseBase;
import cn.bctools.auth.login.auth.wx.enterprise.WxEnterpriseHandler;
import cn.bctools.auth.login.dto.RegisterDto;
import cn.bctools.auth.login.enums.LoginTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.service.*;
import cn.bctools.auth.vo.ResetPasswordDto;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.*;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.redis.utils.RedisUtils;
import cn.bctools.web.utils.IpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = "用户登录信息")
@RestController
@RequestMapping("index")
@AllArgsConstructor
public class UserIndexController {

    public static final String MSG_BIND = "绑定成功";
    public static final String MSG_UNBIND = "解绑成功";
    public static final String STRING_BLANK = " ";

    WxLoginHandler wxLoginHandler;
    WxAppLoginHandler wxAppLoginHandler;
    WxOfficialAccountsLoginHandler wxOfficialAccountsLoginHandler;
    WxEnterpriseHandler wxEnterpriseHandler;
    DdScanLoginHandler ddScanLoginHandler;
    LdapLoginHandler ldapLoginHandler;
    SmsEmailComponent smsComponent;
    UserInfoComponent userInfoComponent;
    PasswordEncoder passwordEncoder;
    ApplyService applyService;
    RedisUtils redisUtils;
    RoleService roleService;
    UserService userService;
    TenantService tenantService;
    UserLevelService userLevelService;
    UserTenantService userTenantService;
    UserExtensionService userExtensionService;
    BulletinService bulletinService;
    SysConfigService sysConfigService;
    PermissionComponent permissionProcessService;
    OpinionService opinionService;

    @Log(back = false)
    @ApiOperation("获取公告")
    @GetMapping("/bulletin/{appKey}")
    public R<Bulletin> bulletins(@PathVariable String appKey) {
        //处理移动端和PC端的显示问题
        BulletinTypeEnum bulletinTypeEnum = IpUtil.isMobile() ? BulletinTypeEnum.MOBILE : BulletinTypeEnum.PC;
        List<Bulletin> list = bulletinService.list(new LambdaQueryWrapper<Bulletin>()
                .select(Bulletin::getTitle, Bulletin::getContent, Bulletin::getContentType, Bulletin::getTitle)
                .eq(Bulletin::getPublish, true)
                .eq(Bulletin::getType, bulletinTypeEnum)
                //结束时间大于当前时间
                .gt(Bulletin::getEndTime, DateUtil.date())
                //开始时间大于大前时间
                .lt(Bulletin::getStartTime, DateUtil.date())
                .like(Bulletin::getAppKeys, appKey));
        if (ObjectNull.isNotNull(list)) {
            return R.ok(list.get(0));
        }
        return R.ok();
    }

    @Log(back = false)
    @ApiOperation("获取当前登录用户所在租户信息")
    @GetMapping("/this/tenant")
    public R<TenantPo> thisTenant() {
        String tenantId = TenantContextHolder.getTenantId();
        TenantPo byId = tenantService.getById(tenantId);
        //获取当前登录用户的租户信息的首页
        String level = UserCurrentUtils.getCurrentUser().getLevel();
        if (ObjectNull.isNotNull(level)) {
            //放入用户登录首页
            UserLevel one = userLevelService.getOne(Wrappers.query(new UserLevel().setName(level)));
            if (ObjectNull.isNotNull(one)) {
                String index_url = one.getIndexUrl();
                byId.setDefaultIndexUrl(index_url);
            }
        }
        return R.ok(byId);
    }

    @Log(back = false)
    @GetMapping("/menu/{appKey}")
    @ApiOperation("用户登录后获取当前菜单")
    public R<List<Tree<Object>>> menu(@PathVariable String appKey) {
        // 获取用户角色，获取此角色所有资源
        String userId = UserCurrentUtils.getUserId();
        List menus = new ArrayList();
        // 普通菜单
        List<Tree<Object>> normalMenus = permissionProcessService.getPermission(userId, appKey);
        if (ObjectNull.isNotNull(normalMenus)) {
            menus.addAll(normalMenus);
        }
        return R.ok(menus);
    }

    @Log(back = false)
    @GetMapping("/menu/{search}/{appKey}")
    @ApiOperation("菜单搜索")
    public R<List<TreePo>> search(@PathVariable String appKey, @PathVariable String search) {
        // 获取用户角色，获取此角色所有资源
        String userId = UserCurrentUtils.getUserId();
        Apply apply = applyService.getOne(Wrappers.<Apply>lambdaQuery().select(Apply::getId).eq(Apply::getEnable, true).eq(Apply::getAppKey, appKey), false);
        List menus = permissionProcessService.getMenus(userId, apply)
                .stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getUrl()))
                .filter(e -> e.getName().startsWith(search) || PinyinUtils.getAllFirstLetter(e.getName()).startsWith(search))
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());

        return R.ok(menus);
    }

    @Log
    @ApiOperation("获取当前用户信息")
    @GetMapping("/user/info")
    public R<UserDto> userInfo() {
        UserDto user = UserCurrentUtils.getCurrentUser();
        // 获取用户扩展信息
        List<UserExtension> extensions = userExtensionService.list(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, user.getId()));
        Map<String, Object> collect = extensions.stream().collect(Collectors.toMap(UserExtension::getType, UserExtension::getExtension));
        // 数据处理
        if (MapUtils.isNotEmpty(collect)) {
            collect.entrySet().stream().filter(c -> c.getKey().equals(LoginTypeEnum.WECHAT_MP.getValue()))
                    .forEach(c -> {
                        Map<String, Object> map = JSON.parseObject(JSON.toJSONString(c.getValue()));
                        if (ObjectNull.isNull(map.get("nickname"))) {
                            map.put("nickname", "微信用户");
                        }
                        c.setValue(map);
                    });
        }
        User byId = userService.getById(user.getId());
        UserDto userInfoDto = userInfoComponent.getUserInfoDto(byId, user.getTenantId());
        userInfoDto.setExceptions(collect);
        return R.ok(userInfoDto);
    }

    @Log
    @ApiOperation("获取当前用户信息")
    @PostMapping("/user/info")
    public R<UserDto> userInfo(@RequestBody RegisterDto registerDto) {

        //校验是否成功
        smsComponent.check(registerDto.getPhone(), registerDto.getCode(), () -> new BusinessException("验证失败"));

        UserDto user = UserCurrentUtils.getCurrentUser();
        // 获取用户扩展信息
        List<UserExtension> extensions = userExtensionService.list(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, user.getId()));
        Map<String, Object> collect = extensions.stream().collect(Collectors.toMap(UserExtension::getType, UserExtension::getExtension));
        User byId = userService.getById(user.getId());
        UserDto userInfoDto = userInfoComponent.getUserInfoDto(byId, user.getTenantId());
        userInfoDto.setExceptions(collect);
        return R.ok(userInfoDto);
    }

    @Log
    @ApiOperation("完善信息")
    @GetMapping("/user/complete")
    public R<UserDto> complete() {
        UserDto user = UserCurrentUtils.getCurrentUser();
        // 获取用户扩展信息
        List<UserExtension> extensions = userExtensionService.list(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, user.getId()));
        Map<String, Object> collect = extensions.stream().collect(Collectors.toMap(UserExtension::getType, UserExtension::getExtension));
        User byId = userService.getById(user.getId());
        UserDto userInfoDto = userInfoComponent.getUserInfoDto(byId, user.getTenantId());
        userInfoDto.setExceptions(collect);
        return R.ok(userInfoDto);
    }

    @Log
    @ApiOperation("修改账号")
    @PutMapping("/update/account")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> updateAccount(@RequestParam String accountName) {
        if (StringUtils.isBlank(accountName)) {
            return R.failed("账号不能为空");
        }
        if (accountName.contains(STRING_BLANK)) {
            return R.failed("账号不能为空包含空格");
        }
        String userId = UserCurrentUtils.getUserId();
        int count = userService.count(Wrappers.<User>lambdaQuery()
                .ne(User::getId, userId)
                .eq(User::getAccountName, accountName)
                .last(SysConstant.FOR_UPDATE));
        if (count >= 1) {
            return R.failed("该账号名已被使用");
        }
        userService.updateInfo(User::getAccountName, accountName, userId);
        return R.ok(true, "修改成功");
    }

    @Log
    @ApiOperation("修改昵称")
    @PutMapping("/update/realName")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> updateRealName(@RequestParam String realName) {
        if (StringUtils.isBlank(realName)) {
            return R.failed("昵称不能为空");
        }
        if (realName.contains(STRING_BLANK)) {
            return R.failed("昵称不能为空包含空格");
        }
        String userId = UserCurrentUtils.getUserId();
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getRealName, realName)
                .eq(UserTenant::getUserId, userId));
        userService.updateInfo(User::getRealName, realName, userId);
        return R.ok(true, "修改成功");
    }

    /**
     * 绑定手机号，
     */
    @Log
    @ApiOperation("绑定手机")
    @PutMapping("/bind/phone")
    public R<Boolean> bindPhone(@RequestParam String phone, @RequestParam String code) {
        smsComponent.check(phone, code, () -> new BusinessException("验证码不正确"));
        String userId = UserCurrentUtils.getUserId();

        //判断是否是合并关系
        //将原来的设置为空
//        userService.update(Wrappers.<User>lambdaUpdate().set(User::getPhone, phone)
//                .eq(User::getPhone, phone));
//        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getPhone, phone)
//                .eq(UserTenant::getPhone, phone));

        userService.updateInfo(User::getPhone, phone, userId);
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getPhone, phone)
                .eq(UserTenant::getUserId, userId));
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("解除手机绑定")
    @DeleteMapping("/bind/phone")
    public R<Boolean> unbindPhone() {
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getPhone, null, userId);
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getPhone, null)
                .eq(UserTenant::getUserId, userId));
        return R.ok(true, MSG_UNBIND);
    }

    @Log
    @ApiOperation("绑定微信")
    @PutMapping("/bind/wx")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindWx(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        AuthConfigUtil.setCurrentAppId(appId);
        wxOfficialAccountsLoginHandler.bind(user, code, appId);
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("绑定小程序")
    @PutMapping("/bind/wxapp")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindWxApp(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        AuthConfigUtil.setCurrentAppId(appId);
        wxAppLoginHandler.bind(user, code, appId);
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("解除微信绑定")
    @DeleteMapping("/bind/wx")
    public R<Boolean> bindWx() {
        String userId = UserCurrentUtils.getUserId();
        userExtensionService.remove(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, userId)
                .eq(UserExtension::getType, LoginTypeEnum.WECHAT_MP.getValue()));
        return R.ok(true, MSG_UNBIND);
    }

    @Log
    @ApiOperation("绑定企业微信")
    @PutMapping("/bind/wxenterprise")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindWxEnterprise(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        AuthConfigUtil.setCurrentAppId(appId);
        wxEnterpriseHandler.bind(user, code, appId);
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("解除企业微信绑定")
    @DeleteMapping("/bind/wxenterprise")
    public R<Boolean> bindWxEnterprise() {
        String userId = UserCurrentUtils.getUserId();
        userExtensionService.remove(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, userId)
                .eq(UserExtension::getType, WxEnterpriseBase.LOGIN_TYPE));
        return R.ok(true, MSG_UNBIND);
    }

    @Log
    @ApiOperation("绑定钉钉")
    @PutMapping("/bind/ding")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindDing(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        AuthConfigUtil.setCurrentAppId(appId);
        ddScanLoginHandler.bind(user, code, appId);
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("解除钉钉绑定")
    @DeleteMapping("/bind/ding")
    public R<Boolean> bindDing() {
        String userId = UserCurrentUtils.getUserId();
        userExtensionService.remove(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, userId)
                .eq(UserExtension::getType, DdBase.LOGIN_TYPE));
        return R.ok(true, MSG_UNBIND);
    }

    @Log
    @ApiOperation("绑定LDAP")
    @PutMapping("/bind/ldap")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindLdap(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        AuthConfigUtil.setCurrentAppId(appId);
        ldapLoginHandler.bind(user, code, appId);
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("解除LDAP绑定")
    @DeleteMapping("/bind/ldap")
    public R<Boolean> bindLdap() {
        String userId = UserCurrentUtils.getUserId();
        userExtensionService.remove(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, userId)
                .eq(UserExtension::getType, LoginTypeEnum.LDAP.getValue()));
        return R.ok(true, MSG_UNBIND);
    }

    @Log
    @ApiOperation("发送邮箱验证码")
    @GetMapping("/send/email/code")
    public R<Boolean> codeEmail(@RequestParam String email) {
        UserDto user = UserCurrentUtils.getCurrentUser();
        TenantPo tenantPo = tenantService.getById(user.getTenantId());
        smsComponent.sendEmailCode(user, email, tenantPo);
        return R.ok(true, "发送成功");
    }

    @Log
    @ApiOperation("绑定邮箱")
    @PutMapping("/bind/email")
    public R<Boolean> bindEmail(@RequestParam String email, @RequestParam String code) {
        smsComponent.checkEmailCode(email, code, () -> new BusinessException("验证码不正确"));
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getEmail, email, userId);
        return R.ok(true, MSG_BIND);
    }

    @Log
    @ApiOperation("修改头像")
    @PutMapping("/bind/headImg")
    public R<Boolean> bindEmail(@RequestParam String headImg) {
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getHeadImg, headImg, userId);
        return R.ok(true, "修改成功");
    }

    @Log
    @ApiOperation("解除邮箱绑定")
    @DeleteMapping("/bind/email")
    public R<Boolean> bindEmail() {
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getEmail, null, userId);
        return R.ok(true, "解除成功");
    }

    @Log
    @ApiOperation("设置密码")
    @PostMapping("/user/change/password")
    public R<Boolean> changePassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getRePassword())) {
            return R.failed("密码不匹配");
        }
        // appId为与前端对应的应用id
        String decodedPassword = PasswordUtil.decodedPassword(resetPasswordDto.getRePassword(), SpringContextUtil.getKey());
        PasswordUtil.checkPassword(decodedPassword);
        String encodedPassword = passwordEncoder.encode(decodedPassword);
        // 修改密码
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getPassword, encodedPassword, userId);
        return R.ok(true, "设置密码成功");
    }

    @Log
    @ApiOperation("我的组织")
    @GetMapping("/my/tenants")
    public R<List<TenantPo>> myTenants() {
        String userId = UserCurrentUtils.getUserId();
        List<TenantPo> list = tenantService.list(Wrappers.query(new TenantPo().setEnable(true).setAdminUserId(userId)));
        String clientId = UserCurrentUtils.getCurrentUser().getClientId();
        list.forEach(e -> {
            TenantContextHolder.setTenantId(e.getId());
            Map<String, Object> config = sysConfigService.getConfig(clientId, SysConfigTypeEnum.BASIC);
            if (MapUtils.isNotEmpty(config)) {
                Object logo = config.get("logo");
                if (ObjectNull.isNotNull(logo)) {
                    e.setLogo(String.valueOf(logo));
                }
                Object icon = config.get("icon");
                if (ObjectNull.isNotNull(icon)) {
                    e.setIcon(String.valueOf(icon));
                }
                Object name = config.get("name");
                if (ObjectNull.isNotNull(name)) {
                    e.setName(String.valueOf(name));
                }
            }
        });
        return R.ok(list);
    }

    @Log
    @ApiOperation(value = "解散组织", notes = "解散组织后所有用户都不可登录即组织")
    @DeleteMapping("/my/tenant/{tenantId}")
    public R<Boolean> delete(@PathVariable String tenantId) {
        //校验用户是否是管理员
        String userId = UserCurrentUtils.getUserId();
        if (tenantService.getById(tenantId).getAdminUserId().equals(userId)) {
            tenantService.removeById(tenantId);
        }
        return R.ok(true, "解散成功");
    }

    @Log
    @ApiOperation("已经加入的组织")
    @GetMapping("/tenants")
    @Transactional(rollbackFor = Exception.class)
    public R<List<TenantPo>> tenants() {
        String userId = UserCurrentUtils.getUserId();
        TenantContextHolder.clear();
        // 查询自己所在的组织集合
        List<UserTenant> userTenantList = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().select(UserTenant::getTenantId).eq(UserTenant::getCancelFlag, false).eq(UserTenant::getUserId, userId));
        List<String> tenantIds = userTenantList.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(tenantIds)) {
            return R.ok(Collections.emptyList());
        }
        List<TenantPo> tenantList = tenantService.list(Wrappers.<TenantPo>lambdaQuery()
                .in(TenantPo::getId, tenantIds)
                // 排除自己创建的组织
                .ne(TenantPo::getAdminUserId, userId));
        String clientId = UserCurrentUtils.getCurrentUser().getClientId();
        tenantList.forEach(e -> {
            TenantContextHolder.setTenantId(e.getId());
            Map<String, Object> config = sysConfigService.getConfig(clientId, SysConfigTypeEnum.BASIC);
            if (MapUtils.isNotEmpty(config)) {
                Object logo = config.get("logo");
                if (ObjectNull.isNotNull(logo)) {
                    e.setLogo(String.valueOf(logo));
                }
                Object icon = config.get("icon");
                if (ObjectNull.isNotNull(icon)) {
                    e.setIcon(String.valueOf(icon));
                }
                Object name = config.get("name");
                if (ObjectNull.isNotNull(name)) {
                    e.setName(String.valueOf(name));
                }
            }
        });
        return R.ok(tenantList);
    }

    @Log
    @ApiOperation("退出组织")
    @GetMapping("/tenant/{tenantId}")
    public R<Boolean> tenants(@PathVariable String tenantId) {
        //注销对应组织的帐号
        String userId = UserCurrentUtils.getUserId();
        TenantContextHolder.setTenantId(tenantId);
        userTenantService.clearUser(userId);
        return R.ok(true, "退出成功");
    }

    @Log
    @ApiOperation("是否是平台管理员")
    @GetMapping("/administrator")
    public R<Boolean> administrator() {
        //注销对应组织的帐号
        String tenantId = UserCurrentUtils.getCurrentUser().getTenantId();
        TenantPo byId = tenantService.getById(tenantId);
        //租户的父级是空的，则是最上级管理员
        return R.ok(ObjectNull.isNull(byId.getParentId()));
    }

    @Log
    @ApiOperation("注销帐号")
    @PutMapping("/logoff")
    public R<Boolean> logoff() {
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getCancelFlag, true, userId);
        return R.ok(true, "注销成功");
    }

    @Log
    @ApiOperation("提交意见")
    @PostMapping("/opinion")
    public R opinion(@RequestBody Opinion opinion) {
        String userId = UserCurrentUtils.getUserId();
        opinion.setCreateById(userId);
        opinion.setCreateByHeadImg(UserCurrentUtils.getCurrentUser().getHeadImg());
        opinion.setCreateBy(UserCurrentUtils.getRealName());
        opinionService.save(opinion);
        return R.ok();
    }

}
