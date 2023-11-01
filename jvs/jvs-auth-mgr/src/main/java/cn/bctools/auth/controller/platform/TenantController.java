package cn.bctools.auth.controller.platform;

import cn.bctools.auth.component.TenantDynamicDatasourceComponent;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.AuthorityManagementUtils;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author
 */
@Slf4j
@Api(tags = "组织管理")
@RestController
@RequestMapping("tenant")
@AllArgsConstructor
public class TenantController {

    StringRedisTemplate redisTemplate;

    TenantDynamicDatasourceComponent tenantDynamicDatasourceComponent;
    RoleService roleService;
    UserService userService;
    UserLevelService userLevelService;
    TenantService tenantService;
    UserTenantService userTenantService;

    /**
     * 查询组织为当前用户下的所有组织
     *
     * @param page     分页信息
     * @param tenantPo 查询条件
     * @return 组织列表
     */
    @Log
    @ApiOperation(value = "分页", notes = "获取组织只获取当前管理员及以下的子组织, 不设计其它的组织")
    @GetMapping("/page")
    public R<Page<TenantPo>> page(Page<TenantPo> page, TenantPo tenantPo) {
        if (!UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            return R.ok();
        }
        String name = tenantPo.getName();
        LambdaQueryWrapper<TenantPo> queryWrapper = Wrappers.<TenantPo>lambdaQuery()
                //排除自己
//                .ne(TenantPo::getId, UserCurrentUtils.getCurrentUser().getTenantId())
                .like(StrUtil.isNotBlank(name), TenantPo::getName, name)
                .orderByDesc(TenantPo::getCreateTime);
        tenantService.page(page, queryWrapper);
        Set<String> collect = page.getRecords().stream().map(e -> e.getAdminUserId()).collect(Collectors.toSet());
        Map<String, User> userMap = userService.listByIds(collect).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        for (TenantPo record : page.getRecords()) {
            User userById = userMap.get(record.getAdminUserId());
            record.setAdminUserName(userById.getRealName());
            record.setAdminUserImg(userById.getHeadImg());
        }
        return R.ok(page);
    }

    @Log
    @ApiOperation("详情")
    @GetMapping("/info/{tenantId}")
    public R<TenantPo> get(@PathVariable("tenantId") String tenantId) {
        String userId = UserCurrentUtils.getUserId();
        TenantPo tenantPo = tenantService.getOne(Wrappers.<TenantPo>lambdaQuery()
                .eq(TenantPo::getId, tenantId)
                .eq(TenantPo::getAdminUserId, userId), false);
        UserDto userById = AuthorityManagementUtils.getUserById(userId);
        tenantPo.setAdminUserName(userById.getRealName());
        tenantPo.setAdminUserImg(userById.getHeadImg());
        if (Objects.isNull(tenantPo)) {
            return R.failed("组织不存在或没有操作权限");
        }
        return R.ok(tenantPo);
    }

    /**
     * 修改组织信息
     * <p>
     * 1. 组织名称去重校验
     *
     * @param tenantPo 组织信息
     * @return 修改结果
     */
    @Log
    @ApiOperation("修改一个组织")
    @PutMapping("/info")
    public R<Boolean> put(@RequestBody TenantPo tenantPo) {
        String id = tenantPo.getId();
        if (StringUtils.isBlank(id)) {
            return R.failed("修改失败, id为空");
        }
        if (ObjectNull.isNull(tenantPo.getAdminUserAccount())) {
            return R.failed("请填写管理员帐号");
        }
        if (UserCurrentUtils.getCurrentUser().getPlatformAdmin()) {
            //设置必须设置管理员
            User one = userService.getOne(Wrappers.query(new User().setAccountName(tenantPo.getAdminUserAccount())));
            if (ObjectNull.isNull(one)) {
                return R.failed("管理员帐号不存在");
            }
            tenantPo.setAdminUserId(String.valueOf(one.getId()));
            TenantContextHolder.setTenantId(tenantPo.getId());
            int count = userTenantService.count(Wrappers.query(new UserTenant().setUserId(one.getId())));
            if (count == 0) {
                userTenantService.saveOrUpdate(new UserTenant().setUserId(one.getId()).setRealName(one.getRealName()));
            }
            tenantService.updateById(tenantPo);
            return R.ok(true, "修改成功");
        } else {
            return R.failed("非管理员不允许修改组织");
        }
    }

    @Log
    @ApiOperation(value = "新增组织", notes = "创建组织组织管理员，默认为当前创建人员, 当前用户，必须要完善手机号，为保证业务失败后可以通知管理员进行处理")
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> save(@Valid @RequestBody TenantPo tenantPo) {
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        if (StringUtils.isBlank(currentUser.getPhone())) {
            return R.failed("创建组织必须绑定手机号, 请到个人中心完善手机号");
        }
        if (ObjectNull.isNull(tenantPo.getAdminUserAccount())) {
            return R.failed("请填写管理员帐号");
        }
        String name = tenantPo.getName();
        int count = tenantService.count(Wrappers.<TenantPo>lambdaQuery().eq(TenantPo::getName, name));
        if (count > 0) {
            return R.failed("组织名称重复");
        }
        String tenantId = currentUser.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            //上级组织和当前组织都为0
            TenantContextHolder.setTenantId(currentUser.getId());
            //有可能第一级组织没有组织，则直接将用户ID做为组织ID即可
        } else {
            //设置为用户同级组织
            tenantPo.setParentId(currentUser.getTenantId());
        }
        //设置必须设置管理员
        User one = userService.getOne(Wrappers.query(new User().setAccountName(tenantPo.getAdminUserAccount())));
        if (ObjectNull.isNull(one)) {
            return R.failed("管理员帐号已经存在");
        }
        tenantPo.setAdminUserId(String.valueOf(one.getId()));
        tenantService.save(tenantPo);
        //将组织的信息存放进去
        String newTenantId = tenantPo.getId();
        TenantContextHolder.setTenantId(newTenantId);
        int countUser = userTenantService.count(Wrappers.query(new UserTenant().setUserId(one.getId())));
        if (countUser == 0) {
            userTenantService.saveOrUpdate(new UserTenant().setUserId(one.getId()).setRealName(one.getRealName()));
        }
        // 创建默认的游客角色
        roleService.createDefaultSysRole();
        tenantDynamicDatasourceComponent.init(newTenantId);
        return R.ok(true, "新增成功");
    }

    @Log
    @ApiOperation(value = "禁用或启用组织", notes = "禁用后，组织还可以启用")
    @PutMapping("/enable/{id}/{state}")
    public R<Boolean> enable(@PathVariable("id") String id, @PathVariable("state") Boolean state) {
        String userId = UserCurrentUtils.getUserId();
        TenantPo byId = tenantService.getById(id);
        byId.setEnable(state);
        tenantService.updateById(byId);
        return R.ok(true, state ? "启用成功" : "禁用成功");
    }

    @Log
    @ApiOperation(value = "删除", notes = "删除组织，将导致所有信息无法访问")
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> delete(@PathVariable("id") String id) {
        TenantPo byId = tenantService.getById(id);
        //平台帐号
        if (ObjectNull.isNull(byId.getParentId())) {
            throw new BusinessException("不允许解散平台租户");
        }
        String userId = UserCurrentUtils.getUserId();
        // 只能由管理员删除
        boolean removed = tenantService.remove(Wrappers.<TenantPo>lambdaQuery()
                .eq(TenantPo::getId, id)
                .eq(TenantPo::getAdminUserId, userId));
        if (!removed) {
            return R.failed("组织不存在或没有操作权限");
        }
        //删除组织下所有的用户
        //删除所有的用户
        Set<String> list = userTenantService.list(new LambdaQueryWrapper<UserTenant>().select(UserTenant::getUserId)).stream().map(UserTenant::getUserId).collect(Collectors.toSet());
        userTenantService.removeByIds(list);
        //清除租户
        TenantContextHolder.clear();
        //还有的
        Set<String> userIds = userTenantService.list(new LambdaQueryWrapper<UserTenant>().select(UserTenant::getUserId).in(UserTenant::getUserId, list).groupBy(UserTenant::getUserId)).stream().map(e -> e.getUserId()).collect(Collectors.toSet());
        //删除帐号
        list.removeAll(userIds);
        if (ObjectNull.isNotNull(list)) {
            //删除帐号
            userService.removeByIds(list);
        }
        return R.ok(true, "删除成功");
    }

}
