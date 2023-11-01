package cn.bctools.auth.service.impl;

import cn.bctools.auth.entity.RolePermission;
import cn.bctools.auth.mapper.RolePermissionMapper;
import cn.bctools.auth.service.PermissionService;
import cn.bctools.gateway.entity.Permission;
import cn.bctools.gateway.mapper.PermissionMapper;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 */
@Slf4j
@Service
@AllArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Permission> queryUserPermission(String appId, List<String> rolesIds) {
        List<RolePermission> permissionList = rolePermissionMapper.selectList(Wrappers.<RolePermission>lambdaQuery()
                .in(RolePermission::getRoleId, rolesIds));
        List<String> ids = permissionList
                .stream()
                .map(RolePermission::getPermissionId)
                .filter(ObjectUtil::isNotNull)
                .distinct()
                .collect(Collectors.toList());
        if (ObjectUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.list(Wrappers.<Permission>lambdaQuery()
                .select(Permission::getPermission)
                .eq(Permission::getApplyId, appId)
                .in(Permission::getId, ids));
    }

}
