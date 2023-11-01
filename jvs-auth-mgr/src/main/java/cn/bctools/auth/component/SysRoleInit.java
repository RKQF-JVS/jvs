package cn.bctools.auth.component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.entity.TenantPo;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.service.RoleService;
import cn.bctools.auth.service.TenantService;
import cn.bctools.common.utils.TenantContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统默认角色初始化
 *
 * @Author: GuoZi
 */
@Slf4j
@Component
@AllArgsConstructor
public class SysRoleInit implements ApplicationRunner {

    RoleService roleService;
    TenantService tenantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) throws Exception {
        log.info("[初始化角色] start");
        TenantContextHolder.clear();
        this.init();
        log.info("[初始化角色] end");
    }

    private void init() {
        // 获取所有租户id
        List<TenantPo> tenantList = tenantService.list(Wrappers.<TenantPo>lambdaQuery().select(TenantPo::getId));
        if (ObjectUtils.isEmpty(tenantList)) {
            log.info("[初始化角色] 无租户信息");
            return;
        }
        List<String> tenantIds = tenantList.stream().map(TenantPo::getId).collect(Collectors.toList());
        // 查询已有角色
        List<String> roleNames = SysRoleEnum.getAllNames();
        List<Role> roleList = roleService.list(Wrappers.<Role>lambdaQuery()
                .select(Role::getRoleName, Role::getTenantId)
                .in(Role::getRoleName, roleNames));
        Set<String> roleSet = roleList.stream().map(role -> role.getRoleName() + role.getTenantId()).collect(Collectors.toSet());
        List<Role> roles = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (SysRoleEnum value : SysRoleEnum.values()) {
            String roleName = value.getName();
            for (String tenantId : tenantIds) {
                if (!roleSet.contains(roleName + tenantId)) {
                    roles.add(new Role()
                            .setRoleName(roleName)
                            .setRoleDesc(value.getDesc())
                            .setType(RoleTypeEnum.userRole)
                            .setCreateTime(now)
                            .setUpdateTime(now)
                            .setTenantId(tenantId));
                }
            }
        }
        if (ObjectUtils.isEmpty(roles)) {
            log.info("[初始化角色] 数据完整, 跳过角色创建");
            return;
        }
        roleService.saveBatch(roles);
    }

}
