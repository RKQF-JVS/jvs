package cn.bctools.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.service.*;
import cn.bctools.auth.vo.InviteVo;
import cn.bctools.auth.vo.UserVo;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.redis.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jolokia.util.Base64Util;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @Description: 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Api(tags = "用户管理")
public class UserController {

    RedisUtils redisUtils;
    SmsEmailComponent smsComponent;

    JobService jobService;
    UserService userService;
    DeptService deptService;
    TenantService tenantService;
    UserTenantService userTenantService;

    @Log(back = false)
    @ApiOperation("所有用户的列表")
    @GetMapping("/all")
    public R<List<UserVo>> users() {
        // 查询用户租户信息 TODO 优化查询效率
        List<UserTenant> userTenants = userTenantService.list();
        if (ObjectUtil.isEmpty(userTenants)) {
            return R.ok(Collections.emptyList());
        }
        // 查询用户基本信息
        Map<String, User> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, Function.identity()));
        // User与UserTenant都有cancel_flag字段, 需要以UserTenant的为准, 需要注意这里copy的顺序
        List<UserVo> list = userTenants.stream().map(e -> BeanCopyUtil.copy(UserVo.class, userMap.get(e.getUserId()), e).setId(e.getUserId())).collect(Collectors.toList());
        return R.ok(list);
    }

    @Log
    @ApiOperation(value = "用户列表", notes = "组织机构管理，当前选择部门，下面的人员有哪些，显示到右侧")
    @GetMapping("/page")
    public R<Page<UserVo>> users(Page<UserTenant> page, UserTenant userTenant) {
        userTenant.setTenantId(TenantContextHolder.getTenantId());
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        // 查询用户租户信息  排除自己
        userTenantService.page(page, Wrappers.query(userTenant).lambda().ne(UserTenant::getUserId, currentUser.getId()));
        Page<UserVo> userPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal(), page.isSearchCount());
        List<UserTenant> userTenants = page.getRecords();
        if (ObjectUtil.isEmpty(userTenants)) {
            return R.ok(userPage);
        }
        Set<String> userIds = userTenants.stream().map(UserTenant::getUserId).collect(Collectors.toSet());
        // 查询用户基本信息
        Map<String, User> userMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        // User与UserTenant都有cancel_flag字段, 需要以UserTenant的为准, 需要注意这里copy的顺序
        List<UserVo> list = userTenants.stream().map(e -> BeanCopyUtil.copy(UserVo.class, userMap.get(e.getUserId()), e).setCreateTime(e.getCreateTime()).setId(e.getUserId())).collect(Collectors.toList());
        userPage.setRecords(list);
        return R.ok(userPage);
    }

    /**
     * 用户搜索
     *
     * @param key    真实姓名或手机号(模糊搜索)
     * @param deptId 部门id
     * @return 用户信息集合
     */
    @Log
    @ApiOperation(value = "用户搜索", notes = "用户搜索操作,目前只提供手机号或姓名搜索")
    @GetMapping("/user/search")
    public R<List<User>> search(@RequestParam(required = false) String key, @RequestParam(required = false) String deptId) {
        if (StringUtils.isNotBlank(deptId)) {
            List<String> userIds = userTenantService.list(Wrappers.<UserTenant>lambdaQuery()
                    .select(UserTenant::getUserId)
                    .eq(UserTenant::getDeptId, deptId))
                    .stream().map(UserTenant::getUserId).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(userIds)) {
                return R.ok(userService.listByIds(userIds));
            }
        } else {
            List<User> list = userService.list(Wrappers.<User>lambdaQuery()
                    .select(User::getId, User::getRealName, User::getPhone, User::getHeadImg)
                    .orderByDesc(User::getCreateTime)
                    .like(ObjectUtil.isNotEmpty(key), User::getRealName, key)
                    .or()
                    .like(ObjectUtil.isNotEmpty(key), User::getPhone, key));
            return R.ok(list);
        }
        return R.ok(Collections.emptyList());
    }

    @Log
    @ApiOperation(value = "获取用户详情", notes = "组织机构管理，点击用户详情操作")
    @GetMapping("/user/{userId}")
    public R<UserVo> user(@PathVariable String userId) {
        User user = userService.getById(userId);
        if (Objects.isNull(user)) {
            return R.failed("用户不存在");
        }
        UserTenant userTenant = userTenantService.getOne(Wrappers.<UserTenant>lambdaQuery().eq(UserTenant::getUserId, userId));
        if (Objects.isNull(userTenant)) {
            return R.failed("用户未加入当前组织");
        }
        UserVo userVo = BeanCopyUtil.copy(UserVo.class, user, userTenant);
        return R.ok(userVo);
    }

    @Log
    @ApiOperation(value = "禁用用户", notes = "组织机构管理，可对用户进行移除，为不影响功能使用，只做注销不做删除,目前不支持注销后找回")
    @DeleteMapping("/users/disabled/{userId}")
    public R<Boolean> disabled(@PathVariable String userId) {
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getCancelFlag, true).eq(UserTenant::getUserId, userId));
        return R.ok(true, "禁用成功");
    }

    @Log
    @ApiOperation(value = "启用用户", notes = "组织机构管理，可对用户进行移除，为不影响功能使用，只做注销不做删除,目前不支持注销后找回")
    @DeleteMapping("/users/enable/{userId}")
    public R<Boolean> enable(@PathVariable String userId) {
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate().set(UserTenant::getCancelFlag, false).eq(UserTenant::getUserId, userId));
        return R.ok(true);
    }

    @Log
    @ApiOperation(value = "新增用户", notes = "后台新增的用户，默认类型为1，另外在租户中间表中进行添加一行处理")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public R<UserVo> save(@RequestBody UserVo vo) {
        if (StringUtils.isBlank(vo.getAccountName())) {
            //手机号前面加随机号
            vo.setAccountName(IdGenerator.getIdStr() + vo.getPhone());
        }
        User user = BeanCopyUtil.copy(vo, User.class);
        UserTenant userTenant = BeanCopyUtil.copy(vo, UserTenant.class);
        userService.saveUser(user, userTenant);
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        TenantPo tenantPo = tenantService.getById(currentUser.getTenantId());
        //发送短信,给用户发送邀请通知
        smsComponent.sendUserInvite(currentUser, user, tenantPo);
        //再查询一次返回给前端
        return this.user(user.getId());
    }

    @Log
    @ApiOperation(value = "生成邀请码", notes = "添加用户的两种方式，通过邀请码进入和通过直接添加,邀请生成规则为租户域名,加租户时间加密串")
    @GetMapping("/get/invite")
    public R<InviteVo> invite(@RequestParam String uri) {
        //默认设置为10分钟有效
        String key = SysConstant.INVITE + IdGenerator.getId();
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String url = uri + key;

        QrCodeUtil.generate(url, 500, 500, "png", out);
        String base64Img = "data:image/png;base64," + Base64Util.encode(out.toByteArray());
        // 过期时间30分钟
        redisUtils.setExpire(key, currentUser, 30, TimeUnit.MINUTES);
        InviteVo inviteVo = new InviteVo().setBase64(base64Img).setUrl(url);
        return R.ok(inviteVo);
    }

    @Log
    @ApiOperation(value = "修改用户", notes = "后台修改用户")
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody UserVo vo) {
        String tenantId = TenantContextHolder.getTenantId();
        User user = BeanCopyUtil.copy(vo, User.class);
        // 手机号校验
        String phone = user.getPhone();
        if (StringUtils.isNotBlank(phone)) {
            int count = userService.count(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone).ne(User::getId, user.getId()));
            if (count >= 1) {
                return R.failed("手机号已被使用");
            }
        }
        UserTenant userTenant = BeanCopyUtil.copy(vo, UserTenant.class);
        String deptId = userTenant.getDeptId();
        if (ObjectUtil.isNotEmpty(deptId)) {
            Dept dept = deptService.getById(deptId);
            if (Objects.isNull(dept)) {
                log.error("该部门不存在, 部门id: {}", deptId);
                return R.failed("该部门不存在");
            }
            String name = dept.getName();
            userTenant.setDeptName(name);
        }
        String jobId = userTenant.getJobId();
        if (ObjectUtil.isNotEmpty(jobId)) {
            Job job = jobService.getById(jobId);
            if (Objects.isNull(job)) {
                log.error("该岗位不存在, 岗位id: {}", jobId);
                return R.failed("该岗位不存在");
            }
            String name = job.getName();
            userTenant.setJobName(name);
        }
        UserTenant updateUserTenant = userTenantService.getOne(Wrappers.query(new UserTenant().setUserId(user.getId()).setTenantId(tenantId)));
        userTenant.setId(updateUserTenant.getId());
        userTenantService.updateById(userTenant);
        userService.updateById(user);
        return R.ok();
    }

}
