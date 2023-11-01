package cn.bctools.auth.controller.platform;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.TenantService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.vo.ResetPasswordDto;
import cn.bctools.common.utils.*;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author 
 */
@Slf4j
@RestController
@RequestMapping("/account")
@AllArgsConstructor
@Api(tags = "帐号管理")
public class AccountController {

    UserService userService;
    PasswordEncoder passwordEncoder;
    UserTenantService userTenantService;
    TenantService tenantService;

    @Log(back = false)
    @ApiOperation("帐号管理")
    @GetMapping("/page")
    public R<Page<User>> page(Page<User> page, User dto) {
        if (UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            //排除自己
            userService.page(page, Wrappers.lambdaQuery(dto).ne(User::getId, UserCurrentUtils.getUserId()).orderByDesc(User::getCreateTime));
            if (ObjectNull.isNull(page.getRecords())) {
                return R.ok(page);
            }
            List<String> collect = page.getRecords().stream().map(e -> e.getId()).collect(Collectors.toList());
            TenantContextHolder.clear();
            Map<String, List<String>> tenantIdMap = userTenantService.list(new LambdaQueryWrapper<UserTenant>().select(UserTenant::getTenantId, UserTenant::getUserId).eq(UserTenant::getCancelFlag, false).in(UserTenant::getUserId, collect))
                    .stream().collect(Collectors.groupingBy(UserTenant::getUserId, Collectors.mapping(e -> e.getTenantId(), Collectors.toList())));
            page.getRecords().forEach(e -> {
                if (tenantIdMap.containsKey(e.getId())) {
                    List<String> strings = tenantIdMap.get(e.getId());
                    List<TenantPo> tenantPos = tenantService.list(new LambdaQueryWrapper<TenantPo>().in(TenantPo::getId, strings).select(TenantPo::getName, TenantPo::getShortName));
                    e.setTenantPo(tenantPos);
                }
            });
            return R.ok(page);
        }
        return R.ok(page);
    }

    @Log
    @ApiOperation(value = "超级管理员修改用户密码", notes = "只有超级管理员才能修改用户密码")
    @PostMapping("/users/update/password/{userId}")
    public R passWord(@RequestBody ResetPasswordDto resetPasswordDto, @PathVariable String userId) {
        //只有平台超级管理员才能修改
        if (UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getRePassword())) {
                return R.failed("密码不匹配");
            }
            // appId为与前端对应的应用id
            String decodedPassword = PasswordUtil.decodedPassword(resetPasswordDto.getRePassword(), SpringContextUtil.getKey());
            PasswordUtil.checkPassword(decodedPassword);
            String encodedPassword = passwordEncoder.encode(decodedPassword);
            User byId = userService.getById(userId);
            if (ObjectNull.isNull(byId)) {
                return R.failed("用户不存在");
            } else {
                // 修改密码
                userService.updateInfo(User::getPassword, encodedPassword, userId);
                return R.ok().setMsg("密码修改成功");
            }
        } else {
            return R.ok();
        }

    }

    @Log
    @ApiOperation(value = "注销用户", notes = "用户无法再进行登录操作")
    @DeleteMapping("/users/disabled/{userId}")
    public R<Boolean> disabled(@PathVariable String userId) {
        userService.update(Wrappers.<User>lambdaUpdate().set(User::getCancelFlag, true).eq(User::getId, userId));
        return R.ok(true, "注销用户");
    }

    @Log
    @ApiOperation(value = "恢复用户", notes = "恢复用户后 ，可以再进行用户登录操作")
    @DeleteMapping("/users/enable/{userId}")
    public R<Boolean> enable(@PathVariable String userId) {
        userService.update(Wrappers.<User>lambdaUpdate().set(User::getCancelFlag, false).eq(User::getId, userId));
        return R.ok(true);
    }


}
