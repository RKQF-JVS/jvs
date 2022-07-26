package cn.bctools.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserRole;
import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.mapper.RoleMapper;
import cn.bctools.auth.mapper.UserMapper;
import cn.bctools.auth.mapper.UserRoleMapper;
import cn.bctools.auth.service.UserRoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色
 *
 * @author
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    RoleMapper roleMapper;
    UserMapper userMapper;

    @Override
    public List<Role> getRoleByUserId(String userId) {
        List<UserRole> list = list(Wrappers.query(new UserRole().setUserId(userId)));
        if (ObjectUtil.isNotEmpty(list)) {
            List<String> collect = list.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            return roleMapper.selectBatchIds(collect);
        }
        return Collections.emptyList();
    }

    @Override
    public List<User> getUsersByRoleId(String roleId) {
        List<UserRole> list = list(Wrappers.query(new UserRole().setRoleId(roleId)));
        if (ObjectUtil.isNotEmpty(list)) {
            List<String> collect = list.stream().map(UserRole::getUserId).collect(Collectors.toList());
            return userMapper.selectBatchIds(collect);
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grandDefaultSysRole(String userId) {
        if (StringUtils.isBlank(userId)) {
            log.warn("[赋予默认角色] 操作失败, 用户id为空");
            return;
        }
        List<Role> roleList = roleMapper.selectList(Wrappers.<Role>lambdaQuery().in(Role::getRoleName, SysRoleEnum.getAutoGrantNames()));
        if (ObjectUtils.isEmpty(roleList)) {
            log.warn("[赋予默认角色] 操作失败, 有找到默认角色");
            return;
        }
        List<UserRole> userRoleList = roleList.stream()
                .map(role -> new UserRole().setUserId(userId).setRoleId(role.getId()))
                .collect(Collectors.toList());
        this.saveBatch(userRoleList);
    }

    @Override
    public void clearUser(@NotNull String userId) {
        this.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId));
    }

}
