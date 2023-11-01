package cn.bctools.auth.controller;

import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.component.PermissionComponent;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.component.UserInfoComponent;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.login.WxAppLoginHandler;
import cn.bctools.auth.login.WxLoginHandler;
import cn.bctools.auth.login.dto.RegisterDto;
import cn.bctools.auth.service.*;
import cn.bctools.auth.vo.ResetPasswordDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.*;

import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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


    WxLoginHandler wxLoginHandler;
    WxAppLoginHandler wxAppLoginHandler;
    SmsEmailComponent smsComponent;
    UserInfoComponent userInfoComponent;
    PasswordEncoder passwordEncoder;

    RoleService roleService;
    UserService userService;
    TenantService tenantService;
    UserTenantService userTenantService;
    UserExtensionService userExtensionService;
    BulletinService bulletinService;
    PermissionComponent permissionProcessService;

    @Log(back = false)
    @ApiOperation("获取公告")
    @GetMapping("/bulletin/{appKey}")
    public R<List<Bulletin>> bulletins(@PathVariable String appKey) {
        List<Bulletin> list = bulletinService.list(new LambdaQueryWrapper<Bulletin>()
                .select(Bulletin::getTitle, Bulletin::getContent)
                .eq(Bulletin::getPublish, true)
                //结束时间大于当前时间
                .gt(Bulletin::getEndTime, DateUtil.date())
                //开始时间大于大前时间
                .lt(Bulletin::getStartTime, DateUtil.date())
                .like(Bulletin::getAppKeys, appKey));
        return R.ok(list);
    }

    @Log(back = false)
    @ApiOperation("获取当前登录用户所在租户信息")
    @GetMapping("/this/tenant")
    public R<TenantPo> thisTenant() {
        String tenantId = TenantContextHolder.getTenantId();
        return R.ok(tenantService.getById(tenantId));
    }

    @Log(back = false)
    @GetMapping("/menu/{appKey}")
    @ApiOperation("用户登录后获取当前菜单")
    public R<List<Tree<Object>>> menu(@PathVariable String appKey) {
        // 获取用户角色，获取此角色所有资源
        String userId = UserCurrentUtils.getUserId();
        List permission = new ArrayList();
        List<Tree<Object>> permission1 = permissionProcessService.getPermission(userId, appKey);
        if (ObjectNull.isNotNull(permission1)) {
            permission.addAll(permission1);
        }
        // 判断当前用户是否为超级管理员
        List<String> currentRoleIds = UserCurrentUtils.getRole();
        List<Role> list = roleService.listByIds(currentRoleIds);
        boolean isAdmin = list.stream().anyMatch(e -> SysRoleEnum.APP_ADMIN.getName().equals(e.getRoleName()) || SysRoleEnum.SYS_ADMIN.getName().equals(e.getRoleName()));

        return R.ok(permission);
    }

    @Log(back = false)
    @GetMapping("/menu/{search}/{appKey}")
    @ApiOperation("菜单搜索")
    public R<List<TreePo>> search(@PathVariable String appKey, @PathVariable String search) {
        // 获取用户角色，获取此角色所有资源
        String userId = UserCurrentUtils.getUserId();
        List menus = permissionProcessService.getMenus(userId, appKey)
                .stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getUrl()))
                .filter(e -> e.getName().startsWith(search) || PinyinUtils.getAllFirstLetter(e.getName()).startsWith(search))
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());

        List<String> currentRoleIds = UserCurrentUtils.getRole();
        List<Role> list = roleService.listByIds(currentRoleIds);
        boolean isAdmin = list.stream().anyMatch(e -> SysRoleEnum.APP_ADMIN.getName().equals(e.getRoleName()) || SysRoleEnum.SYS_ADMIN.getName().equals(e.getRoleName()));

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
    @ApiOperation("绑定手机")
    @PutMapping("/bind/phone")
    public R<Boolean> bindPhone(@RequestParam String phone, @RequestParam(required = false) String name, @RequestParam String code) {
        smsComponent.check(phone, code, () -> new BusinessException("验证码不正确"));
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getPhone, phone, userId);
        if (ObjectNull.isNotNull(name)) {
            userService.updateInfo(User::getRealName, name, userId);
        }
        return R.ok(true, "绑定成功");
    }

    @Log
    @ApiOperation("解除手机绑定")
    @DeleteMapping("/bind/phone")
    public R<Boolean> bindPhone() {
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getPhone, null, userId);
        return R.ok(true, "解除成功");
    }

    @Log
    @ApiOperation("绑定微信")
    @PutMapping("/bind/wx")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindWx(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        wxLoginHandler.bind(user, code, appId);
        return R.ok(true, "绑定成功");
    }

    @Log
    @ApiOperation("绑定小程序")
    @PutMapping("/bind/wxapp")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> bindWxApp(@RequestParam String code, @RequestParam String appId) {
        User user = userService.getById(UserCurrentUtils.getUserId());
        wxAppLoginHandler.bind(user, code, appId);
        return R.ok(true, "绑定成功");
    }

    @Log
    @ApiOperation("解除微信绑定")
    @DeleteMapping("/bind/wx")
    public R<Boolean> bindWx() {
        String userId = UserCurrentUtils.getUserId();
        userExtensionService.remove(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getUserId, userId)
                .eq(UserExtension::getType, AuthDefaultSource.WECHAT_OPEN.name()));
        return R.ok(true, "绑定成功");
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
        return R.ok(true, "绑定成功");
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
        List<TenantPo> list = tenantService.list(Wrappers.query(new TenantPo().setAdminUserId(userId)));
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
        // 查询自己所在的组织集合
        List<UserTenant> userTenantList = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().select(UserTenant::getTenantId).eq(UserTenant::getUserId, userId));
        List<String> tenantIds = userTenantList.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(tenantIds)) {
            return R.ok(Collections.emptyList());
        }
        List<TenantPo> tenantList = tenantService.list(Wrappers.<TenantPo>lambdaQuery()
                .in(TenantPo::getId, tenantIds)
                // 排除自己创建的组织
                .ne(TenantPo::getAdminUserId, userId));
        return R.ok(tenantList);
    }

    @Log
    @ApiOperation("退出组织")
    @GetMapping("/tenant/{tenantId}")
    public R<Boolean> tenants(@PathVariable String tenantId) {
        //注销对应组织的帐号
        String userId = UserCurrentUtils.getUserId();
        userTenantService.remove(Wrappers.<UserTenant>lambdaQuery()
                .eq(UserTenant::getUserId, userId)
                .eq(UserTenant::getTenantId, tenantId)
                .eq(UserTenant::getCancelFlag, true));
        return R.ok(true, "退出成功");
    }

    @Log
    @ApiOperation("注销帐号")
    @PutMapping("/logoff")
    public R<Boolean> logoff() {
        String userId = UserCurrentUtils.getUserId();
        userService.updateInfo(User::getCancelFlag, true, userId);
        return R.ok(true, "注销成功");
    }

}
