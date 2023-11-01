package cn.bctools.auth.service.impl;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.AuthSourceEnum;
import cn.bctools.auth.mapper.DeptMapper;
import cn.bctools.auth.service.DeptService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.util.SyncOrgUtils;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.TreeUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部门服务
 *
 * @author
 */
@Slf4j
@Service
@AllArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    UserTenantService userTenantService;

    @Override
    public void clearUser(@NotNull String userId) {
        this.update(Wrappers.<Dept>lambdaUpdate().set(Dept::getLeaderId, null).eq(Dept::getLeaderId, userId));
    }

    @Override
    public Dept checkId(String deptId) {
        if (StringUtils.isBlank(deptId)) {
            throw new BusinessException("部门id为空, 操作失败");
        }
        Dept dept = this.getById(deptId);
        if (Objects.isNull(dept)) {
            log.error("该部门不存在, 部门id: {}", deptId);
            throw new BusinessException("该部门不存在");
        }
        return dept;
    }

    @Override
    public List<String> getAllChildId(String deptId) {
        if (StringUtils.isBlank(deptId)) {
            return new ArrayList<>();
        }
        List<Dept> allDeptList = this.list(Wrappers.<Dept>lambdaQuery().select(Dept::getId, Dept::getParentId));
        if (ObjectUtils.isEmpty(allDeptList)) {
            return new ArrayList<>();
        }
        List<Dept> passingBy = TreeUtils.getListPassingBy(allDeptList, deptId, Dept::getId, Dept::getParentId);
        return passingBy.stream().map(Dept::getId).collect(Collectors.toList());
    }

    @Override
    public void pull(UserDto userDto, List<Dept> remoteDepts) {
        if (CollectionUtils.isEmpty(remoteDepts)) {
            log.info("无需要同步的部门");
            return;
        }
        // 为了避免id冲突，重新为id赋值。
        remoteDepts.forEach(dept -> {
            dept.setId(SyncOrgUtils.buildDeptId(userDto.getTenantId(), dept.getId()));
            if (Boolean.FALSE.equals(userDto.getTenantId().equals(dept.getParentId()))) {
                dept.setParentId(SyncOrgUtils.buildDeptId(userDto.getTenantId(), dept.getParentId()));
            }
        });

        List<Dept> oldDepts = list();
        // 本地无部门，则同步三方系统部门
        if (CollectionUtils.isEmpty(oldDepts)) {
            saveBatch(remoteDepts);
            return;
        }

        AuthSourceEnum source = remoteDepts.get(0).getSource();
        // 先将当前来源已删除的部门恢复到未删除状态
        String remoteDeptIds = remoteDepts.stream().map(Dept::getId).collect(Collectors.joining("','", "('", "')"));
        baseMapper.updateDelFlag(Boolean.FALSE, source.name(), remoteDeptIds);

        List<String> oldDeptIds = oldDepts.stream().map(Dept::getId).collect(Collectors.toList());
        // 若来源不同，则先删除已有部门，再同步三方系统部门
        if (oldDepts.stream().noneMatch(dept -> source.equals(dept.getSource()))) {
            removeBatch(oldDeptIds);
            saveOrUpdateBatch(remoteDepts);
            return;
        }

        // 来源相同，则删除三方系统不存在的部门，再同步三方系统部门
        List<String> removeIds = oldDeptIds.stream().filter(dept -> remoteDepts.stream().noneMatch(remoteDept -> dept.equals(remoteDept.getId()))).collect(Collectors.toList());
        removeBatch(removeIds);
        saveOrUpdateBatch(remoteDepts);
    }

    /**
     * 批量删除部门
     * @param deptIds
     */
    private void removeBatch(List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        // 删除部门信息
        removeByIds(deptIds);
        // 移除该部门下的用户
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getDeptId, null)
                .set(UserTenant::getDeptName, null)
                .in(UserTenant::getDeptId, deptIds));
    }

}
