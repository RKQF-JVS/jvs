package cn.bctools.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.bctools.auth.mapper.PermissionMapper;
import cn.bctools.auth.mapper.RolePermissionMapper;
import cn.bctools.auth.mapper.UserPermissionMapper;
import cn.bctools.auth.entity.Permission;
import cn.bctools.auth.entity.RolePermission;
import cn.bctools.auth.service.PermissionService;
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
    public List<Permission> queryUserPermission(List<String> rolesIds) {
        List<String> ids = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, rolesIds))
                .stream()
                .map(RolePermission::getPermissionId)
                .filter(ObjectUtil::isNotNull)
                .distinct()
                .collect(Collectors.toList());
        if (ObjectUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return listByIds(ids);
    }
}
