package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.AuthRoleServiceApi;
import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.entity.UserRole;
import cn.bctools.auth.service.RoleService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class RoleApiImpl implements AuthRoleServiceApi {

    RoleService roleService;
    UserRoleService userRoleService;

    @Override
    public R<List<SysRoleDto>> getAll() {
        List<Role> list = roleService.list(Wrappers.<Role>lambdaQuery().select(Role::getId, Role::getRoleName, Role::getRoleDesc));
        return R.ok(BeanCopyUtil.copys(list, SysRoleDto.class));
    }

    @Override
    public R<SysRoleDto> getById(String roleId) {
        Role role = roleService.getOne(Wrappers.<Role>lambdaQuery()
                .select(Role::getId, Role::getRoleName, Role::getRoleDesc)
                .eq(Role::getId, roleId));
        return R.ok(BeanCopyUtil.copy(role, SysRoleDto.class));
    }

    @Override
    public R setUser(String roleId, List<String> userIds) {
        List<String> list = userRoleService.list(Wrappers.query(new UserRole().setRoleId(roleId))).stream().map(UserRole::getUserId).collect(Collectors.toList());
        userIds.removeAll(list);
        List<UserRole> collect = userIds.stream().map(e -> new UserRole().setRoleId(roleId).setUserId(e)).collect(Collectors.toList());
        userRoleService.saveBatch(collect);
        return R.ok();
    }

    @Override
    public R<List<SysRoleDto>> getByIds(List<String> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            return R.ok(Collections.emptyList());
        }
        List<Role> list = roleService.list(Wrappers.<Role>lambdaQuery()
                .select(Role::getId, Role::getRoleName, Role::getRoleDesc)
                .in(Role::getId, roleIds));
        return R.ok(BeanCopyUtil.copys(list, SysRoleDto.class));
    }

}
