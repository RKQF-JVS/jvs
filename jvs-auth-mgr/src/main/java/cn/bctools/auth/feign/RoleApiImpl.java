package cn.bctools.auth.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.api.api.AuthRoleServiceApi;
import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.auth.entity.Job;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.service.RoleService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author Administrator
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class RoleApiImpl implements AuthRoleServiceApi {

    RoleService roleService;

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
