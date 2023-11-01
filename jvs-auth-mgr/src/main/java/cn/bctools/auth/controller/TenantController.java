package cn.bctools.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.component.TenantDynamicDatasourceComponent;
import cn.bctools.auth.entity.TenantPo;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.RoleService;
import cn.bctools.auth.service.TenantService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author guojing
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
        String name = tenantPo.getName();
        String userId = UserCurrentUtils.getUserId();
        tenantService.page(page, Wrappers.<TenantPo>lambdaQuery()
                .eq(TenantPo::getAdminUserId, userId)
                .like(StrUtil.isNotBlank(name), TenantPo::getName, name)
                .orderByDesc(TenantPo::getCreateTime));
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
        //判断是否是管理员
        TenantPo tenantPodb = tenantService.getById(tenantPo.getId());
        String adminUserId = tenantPodb.getAdminUserId();
        if (!UserCurrentUtils.getUserId().equals(adminUserId)) {
            return R.failed("非管理员不允许修改组织");
        }
        // 不允许修改的字段
        tenantPo.setParentId(null);
        tenantPo.setAdminUserId(null);
        tenantService.updateById(tenantPo);
        return R.ok(true, "修改成功");
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
        //设置超级管理员为当前用户
        tenantPo.setAdminUserId(currentUser.getId());
        tenantService.save(tenantPo);
        //将组织的信息存放进去
        String newTenantId = tenantPo.getId();
        TenantContextHolder.setTenantId(newTenantId);
        userTenantService.save(new UserTenant().setUserId(currentUser.getId()).setRealName(currentUser.getRealName()));
        // 创建默认的游客角色
        roleService.createDefaultSysRole();
        tenantDynamicDatasourceComponent.init(newTenantId);
        return R.ok(true, "新增成功");
    }

    @Log
    @ApiOperation(value = "禁用或启用组织", notes = "禁用后，组织还可以启用")
    @PutMapping("/enable/{id}/{state}")
    public R<Boolean> enable(@PathVariable String id, @PathVariable Boolean state) {
        String userId = UserCurrentUtils.getUserId();
        boolean updated = tenantService.update(Wrappers.<TenantPo>lambdaUpdate()
                .set(TenantPo::getEnable, state)
                .eq(TenantPo::getId, id)
                .eq(TenantPo::getAdminUserId, userId));
        if (!updated) {
            return R.failed("组织不存在或没有操作权限");
        }
        return R.ok(true, state ? "启用成功" : "禁用成功");
    }

    @Log
    @ApiOperation(value = "删除", notes = "删除组织，将导致所有信息无法访问")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable String id) {
        String userId = UserCurrentUtils.getUserId();
        // 只能由管理员删除
        boolean removed = tenantService.remove(Wrappers.<TenantPo>lambdaQuery()
                .eq(TenantPo::getId, id)
                .eq(TenantPo::getAdminUserId, userId));
        if (!removed) {
            return R.failed("组织不存在或没有操作权限");
        }
        return R.ok(true, "删除成功");
    }

}
