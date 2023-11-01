package cn.bctools.auth.controller;

import cn.bctools.auth.api.dto.SearchDto;
import cn.bctools.auth.api.dto.UserSelectedDto;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.entity.po.TenantUserData;
import cn.bctools.auth.feign.SelectedApiImpl;
import cn.bctools.auth.mapper.UserTenantMapper;
import cn.bctools.auth.service.*;
import cn.bctools.auth.vo.InviteVo;
import cn.bctools.auth.vo.UserTenantPageVo;
import cn.bctools.auth.vo.UserVo;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.*;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.excel.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jolokia.util.Base64Util;
import org.jolokia.util.DateUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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

    public static final int QR_WIDTH = 500;
    public static final int QR_HEIGHT = 500;
    public static final int QR_EXPIRED_MIN = 30;
    /**
     * 单次导入用户数量限制
     */
    private static final int IMPORT_USER_MAX = 1000;

    RedisUtils redisUtils;
    SmsEmailComponent smsComponent;
    JobService jobService;
    UserService userService;
    SelectedApiImpl selectedApi;
    DeptService deptService;
    TenantService tenantService;
    UserTenantService userTenantService;
    UserRoleService userRoleService;

    @Log(back = false)
    @ApiOperation("所有用户的列表")
    @GetMapping("/all")
    public R<List<UserVo>> users() {
        // 查询用户租户信息 此接口现在暂时没用，只有项目管理在用
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

    @Log(back = false)
    @ApiOperation("所有用户的列表")
    @PostMapping("/all")
    public R<List<UserVo>> users(@RequestBody List<String> ids) {
        //传递参数为空
        if (ObjectNull.isNull(ids)) {
            return R.ok();
        }
        // 查询用户租户信息
        List<UserTenant> userTenants = userTenantService.list(new LambdaQueryWrapper<UserTenant>().in(UserTenant::getUserId, ids));
        if (ObjectUtil.isEmpty(userTenants)) {
            return R.ok(Collections.emptyList());
        }
        // 查询用户基本信息
        Map<String, User> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, Function.identity()));
        // User与UserTenant都有cancel_flag字段, 需要以UserTenant的为准, 需要注意这里copy的顺序
        List<UserVo> list = userTenants.stream().map(e -> BeanCopyUtil.copy(UserVo.class, userMap.get(e.getUserId()), e).setId(e.getUserId())).collect(Collectors.toList());
        return R.ok(list);
    }

    @Log(back = false)
    @ApiOperation(value = "用户列表", notes = "组织机构管理，当前选择部门，下面的人员有哪些，显示到右侧")
    @GetMapping("/page")
    public R<Page<UserVo>> users(Page<TenantUserData> page, UserTenantPageVo userTenant) {
        Page<UserVo> userVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal(), page.isSearchCount());

        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        QueryWrapper queryWrapper = Wrappers.query()
                .like(ObjectNull.isNotNull(userTenant.getRealName()), UserTenantMapper.SYS_USER_TENANT_ALIAS + ".real_name", userTenant.getRealName())
                .like(ObjectNull.isNotNull(userTenant.getDeptId()), UserTenantMapper.SYS_USER_TENANT_ALIAS + ".dept_id", userTenant.getDeptId())
                .like(ObjectNull.isNotNull(userTenant.getPhone()), UserTenantMapper.SYS_USER_TENANT_ALIAS + ".phone", userTenant.getPhone())
                .like(ObjectNull.isNotNull(userTenant.getAccountName()), UserTenantMapper.SYS_USER_ALIAS + ".account_name", userTenant.getAccountName())
                .eq(ObjectNull.isNotNull(userTenant.getSex()), UserTenantMapper.SYS_USER_ALIAS + ".sex", userTenant.getSex())
                .eq(UserTenantMapper.SYS_USER_TENANT_ALIAS + ".cancel_flag", userTenant.getCancelFlag())
                .ne(UserTenantMapper.SYS_USER_TENANT_ALIAS + ".user_id", currentUser.getId());
        userTenantService.tenantUsers(page, queryWrapper);
        if (ObjectUtil.isEmpty(page.getRecords())) {
            return R.ok(userVoPage);
        }

        Map<String, UserVo> map = new LinkedHashMap<>(page.getRecords().size());
        page.getRecords().forEach(e -> {
            UserVo userVo = new UserVo();
            BeanCopyUtil.copy(e, userVo);
            userVo.setId(e.getUserId());
            map.put(userVo.getId(), userVo);
        });
        Map<String, List<Role>> roleByUserId = userRoleService.getRoleByUserId(map.keySet());
        List list = new ArrayList();
        for (String s : map.keySet()) {
            UserVo userVo = map.get(s).setRoleNames(roleByUserId.get(s).stream().map(e -> e.getRoleName()).collect(Collectors.toList()));
            list.add(userVo);
        }
        userVoPage.setRecords(list);
        userVoPage.setTotal(page.getTotal());
        return R.ok(userVoPage);
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
    @ApiOperation(value = "用户选择", notes = "多纬度选择对象")
    @PostMapping("/user/selected")
    public R<UserSelectedDto> search(@RequestBody SearchDto searchDto) {
        return selectedApi.search(searchDto);
    }

    @Log
    @ApiOperation(value = "彻底删除用户", notes = "把用户从当前租户下删除")
    @DeleteMapping("/user/{id}")
    public R delete(@PathVariable String id) {
        userTenantService.remove(Wrappers.query(new UserTenant().setUserId(id)));
        return R.ok();
    }

    @Log
    @ApiOperation(value = "用户选择", notes = "多纬度选择对象")
    @GetMapping("/user/selected/page")
    public R searchPage(Page<UserTenant> page, @RequestParam(value = "key", required = false) String key) {
        page = userTenantService.page(page, new LambdaQueryWrapper<UserTenant>()
                .select(UserTenant::getId, UserTenant::getUserId, UserTenant::getPhone,UserTenant::getRealName)
                //必须要是未删除的
                .eq(UserTenant::getCancelFlag, false)
                .like(ObjectNull.isNotNull(key), UserTenant::getRealName, key)
                .orderByDesc(UserTenant::getCreateTime)
                .or(e->e.like(ObjectNull.isNotNull(key), UserTenant::getPhone,key).eq(UserTenant::getCancelFlag,false))
        );
        if (ObjectNull.isNull(page.getRecords())) {
            return R.ok(page);
        }
        Map<String, UserTenant> ids = page.getRecords().stream().collect(Collectors.toMap(UserTenant::getUserId, Function.identity()));
        List<User> users = userService.listByIds(ids.keySet());
        Page<User> userPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        userPage.setRecords(users);
        return R.ok(userPage);
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
            vo.setAccountName(IdGenerator.getIdStr(36));
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
    public R<InviteVo> invite() {
        //默认设置为10分钟有效
        String tenantId = UserCurrentUtils.getCurrentUser().getTenantId();
        TenantPo tenantPo = tenantService.getById(tenantId);
        //将邀请码放到缓存中，         过期时间30分钟
        String code = IdGenerator.getIdStr(36).toUpperCase();
        Date date = new Date(new Date().getTime() + 30 * 60 * 1000);
        String format = DateUtils.format(date);
        String invite = SysConstant.redisKey("invite", code);
        InviteVo inviteVo = new InviteVo().setCode(code).setStatus(false).setContent(UserCurrentUtils.getRealName() + " 邀请您参加组织\n" +
                "组织名称：【" + tenantPo.getName() + "】\n" +
                "有效期：" + format + "\n" +
                "\n" +
                "邀请码：\n" +
                code + "\n");
        inviteVo.setTenantId(tenantId);
        redisUtils.setExpire(invite, inviteVo, QR_EXPIRED_MIN, TimeUnit.MINUTES);
        return R.ok(inviteVo);
    }

    @Log
    @ApiOperation(value = "设置邀请码是否需要审核默认不需要")
    @GetMapping("/get/invite/{status}/{code}")
    public R<InviteVo> inviteStatus(@PathVariable Boolean status, @PathVariable String code) {
        InviteVo o = (InviteVo) redisUtils.get(SysConstant.redisKey("invite", code));
        o.setStatus(status);
        String invite = SysConstant.redisKey("invite", code);
        redisUtils.setExpire(invite, o, QR_EXPIRED_MIN, TimeUnit.MINUTES);
        return R.ok();
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
        String accountName = user.getAccountName();
        if (StringUtils.isNotBlank(accountName)) {
            int count = userService.count(Wrappers.<User>lambdaQuery().eq(User::getAccountName, accountName).ne(User::getId, user.getId()));
            if (count >= 1) {
                return R.failed("帐号已经存在");
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


    @ApiOperation("下载模板")
    @GetMapping("/template/excel/download")
    public void downloadExcelTemplate(HttpServletResponse response) {
        userService.downloadExcelTemplate(response);
    }

    @ApiOperation("导入")
    @PostMapping("/import")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> importUser(@RequestParam("file") MultipartFile file) {
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        userService.importUserExcel(currentUser, file);
        return R.ok(true, "导入成功");
    }


}
