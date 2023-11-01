package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.SelectedServiceApi;
import cn.bctools.auth.api.dto.*;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class SelectedApiImpl implements SelectedServiceApi {

    JobService jobService;
    UserService userService;
    DeptService deptService;
    RoleService roleService;
    UserGroupService userGroupService;

    @Override
    public R<UserSelectedDto> search(SearchDto searchDto) {
        boolean jobBoolean = false;
        boolean userBoolean = false;
        boolean roleBoolean = false;
        boolean deptBoolean = false;
        boolean groupBoolean = false;
        if (ObjectNull.isNotNull(searchDto, searchDto.getType(), searchDto.getValue())) {
            switch (searchDto.getType()) {
                case user:
                    userBoolean = true;
                    break;
                case role:
                    roleBoolean = true;
                    break;
                case dept:
                    deptBoolean = true;
                    break;
                case job:
                    jobBoolean = true;
                    break;
                case group:
                    groupBoolean = true;
                    break;
                default:
                    break;
            }
        }

        final List<Dept> list = deptService.list(new LambdaQueryWrapper<Dept>()
                .select(Dept::getId, Dept::getName, Dept::getParentId)
                .like(deptBoolean, Dept::getName, searchDto.getValue())
                .select(Dept::getId, Dept::getName, Dept::getParentId));
        List<SysDeptDto> deptList = BeanCopyUtil.copys(list, SysDeptDto.class);
        // 添加顶级部门节点
        String rootDeptId = TenantContextHolder.getTenantId();
        SysDeptDto root = new SysDeptDto().setId(rootDeptId).setChildList(Collections.emptyList());
        deptList.add(root);
        // 转树形结构
        final SysDeptDto deptDto = TreeUtils.list2Tree(deptList, rootDeptId, SysDeptDto::getId, SysDeptDto::getParentId, SysDeptDto::setChildList);

        final UserSelectedDto userSelectedDto = new UserSelectedDto();
        if (Objects.isNull(deptDto)) {
            userSelectedDto.setDepts(Collections.emptyList());
        } else {
            userSelectedDto.setDepts(deptDto.getChildList());
        }
        List<UserGroup> groups = userGroupService.list(new LambdaQueryWrapper<UserGroup>()
                .select(UserGroup::getId, UserGroup::getName, UserGroup::getUsers)
                .like(groupBoolean, UserGroup::getName, searchDto.getValue()));
        userSelectedDto.setGroups(DozerUtil.mapList(groups, UserGroupDto.class));
        final List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>()
                .select(Role::getId, Role::getRoleName, Role::getRoleDesc)
                .like(roleBoolean, Role::getRoleName, searchDto.getValue()));
        userSelectedDto.setRoles(BeanCopyUtil.copys(roles, SysRoleDto.class));
        final List<Job> jobs = jobService.list(new LambdaQueryWrapper<Job>()
                .select(Job::getId, Job::getName)
                .like(jobBoolean, Job::getName, searchDto.getValue()));
        userSelectedDto.setJobs(BeanCopyUtil.copys(jobs, SysJobDto.class));
        return R.ok(userSelectedDto);
    }
}
