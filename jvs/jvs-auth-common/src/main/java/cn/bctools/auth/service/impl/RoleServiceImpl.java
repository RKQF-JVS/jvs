package cn.bctools.auth.service.impl;

import cn.bctools.auth.api.enums.SysRoleEnum;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.entity.enums.RoleTypeEnum;
import cn.bctools.auth.mapper.RoleMapper;
import cn.bctools.auth.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 *
 * @author
 */
@Service
@AllArgsConstructor
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDefaultSysRole() {
        LocalDateTime now = LocalDateTime.now();
        List<Role> roleList = Arrays.stream(SysRoleEnum.values())
                .map(role -> new Role()
                        .setRoleName(role.getName())
                        .setRoleDesc(role.getDesc())
                        .setType(RoleTypeEnum.userRole)
                        .setCreateTime(now)
                        .setUpdateTime(now))
                .collect(Collectors.toList());
        this.saveBatch(roleList);
    }

}
