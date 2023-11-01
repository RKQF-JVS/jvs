package cn.bctools.auth.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.api.api.AuthDeptServiceApi;
import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.service.DeptService;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TreeUtils;
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
        List<Dept> list = deptService.list(Wrappers.<Dept>lambdaQuery().select(Dept::getId, Dept::getName, Dept::getParentId));
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
        // 添加顶级部门节点
        SysDeptDto root = new SysDeptDto().setId(SysConstant.ROOT_ID).setChildList(Collections.emptyList());
        deptList.add(root);
        // 转树形结构
        TreeUtils.list2Tree(deptList, SysConstant.ROOT_ID, SysDeptDto::getId, SysDeptDto::getParentId, SysDeptDto::setChildList);
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
        if (StringUtils.isBlank(parentId) || SysConstant.ROOT_ID.equals(parentId)) {
            parent = child;
        } else {
            parent = deptService.getOne(Wrappers.<Dept>lambdaQuery()
                    .select(Dept::getId, Dept::getName, Dept::getParentId)
                    .eq(Dept::getId, parentId));
        }
        return R.ok(BeanCopyUtil.copy(parent, SysDeptDto.class));
    }

}
