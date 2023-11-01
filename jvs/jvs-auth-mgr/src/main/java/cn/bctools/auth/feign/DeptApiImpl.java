package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.AuthDeptServiceApi;
import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.auth.api.enums.DeptEnum;
import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.service.DeptService;
import cn.bctools.common.utils.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 部门相关接口
 *
 * @Author: GuoZi
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class DeptApiImpl implements AuthDeptServiceApi {

    DeptService deptService;

    @Override
    public R<List<SysDeptDto>> getAll() {
        List<Dept> list = deptService.list(Wrappers.<Dept>lambdaQuery().select(Dept::getId, Dept::getName, Dept::getParentId, Dept::getLeaderId));
        return R.ok(BeanCopyUtil.copys(list, SysDeptDto.class));
    }

    @Override
    public R<SysDeptDto> getById(String deptId) {
        Dept dept = deptService.getOne(Wrappers.<Dept>lambdaQuery()
                .select(Dept::getId, Dept::getName, Dept::getParentId)
                .eq(Dept::getId, deptId));
        return R.ok(BeanCopyUtil.copy(dept, SysDeptDto.class));
    }

    @Override
    public R<List<SysDeptDto>> getByIds(List<String> deptIds) {
        if (ObjectUtils.isEmpty(deptIds)) {
            return R.ok(Collections.emptyList());
        }
        List<Dept> list = deptService.list(Wrappers.<Dept>lambdaQuery()
                .select(Dept::getId, Dept::getName, Dept::getParentId)
                .in(Dept::getId, deptIds));
        return R.ok(BeanCopyUtil.copys(list, SysDeptDto.class));
    }

    @Override
    public R<List<SysDeptDto>> getAllTree() {
        List<SysDeptDto> deptList = this.getAll().getData();
        // 添加根节点, 顶级部门的上级id默认为当前租户id
        String tenantId = TenantContextHolder.getTenantId();
        SysDeptDto root = new SysDeptDto()
                .setId(tenantId)
                .setChildList(Collections.emptyList());
        deptList.add(root);
        // 转树形结构
        TreeUtils.list2Tree(deptList, tenantId, SysDeptDto::getId, SysDeptDto::getParentId, SysDeptDto::setChildList);
        return R.ok(root.getChildList());
    }

    @Override
    public R<List<SysDeptDto>> getChildList(String deptId) {
        List<Dept> list = deptService.list(Wrappers.<Dept>lambdaQuery()
                .select(Dept::getId, Dept::getName, Dept::getParentId)
                .eq(Dept::getParentId, deptId));
        return R.ok(BeanCopyUtil.copys(list, SysDeptDto.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<SysDeptDto> getParent(String deptId) {
        Dept child = deptService.getById(deptId);
        if (Objects.isNull(child)) {
            return R.ok();
        }
        Dept parent;
        String parentId = child.getParentId();
        if (StringUtils.isBlank(parentId) || TenantContextHolder.getTenantId().equals(parentId)) {
            parent = child;
        } else {
            parent = deptService.getOne(Wrappers.<Dept>lambdaQuery()
                    .select(Dept::getId, Dept::getName, Dept::getParentId, Dept::getLeaderId)
                    .eq(Dept::getId, parentId));
        }
        return R.ok(BeanCopyUtil.copy(parent, SysDeptDto.class));
    }

    @Override
    public R<SysDeptDto> getParentBranchOffice(String deptId) {
        Dept child = deptService.getById(deptId);
        if (Objects.isNull(child)) {
            return R.ok();
        }
        Dept parent = deptService.getOne(Wrappers.<Dept>lambdaQuery()
                .select(Dept::getId, Dept::getName, Dept::getParentId, Dept::getLeaderId)
                .eq(Dept::getId, child.getParentId()));
        if (ObjectNull.isNull(parent)) {
            //返回空
            return R.ok();
        } else {
            if (DeptEnum.branchOffice.equals(parent.getType())) {
                SysDeptDto copy = BeanCopyUtil.copy(parent, SysDeptDto.class);
                return R.ok(copy);
            } else {
                //递归找
                return getParentBranchOffice(parent.getParentId());
            }
        }
    }
}
