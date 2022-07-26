package cn.bctools.auth.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.entity.SysTree;
import cn.bctools.auth.entity.SysTreeSaveDto;
import cn.bctools.auth.service.SysTreeService;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TreeUtils;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : GaoZeXi
 */
@Slf4j
@AllArgsConstructor
@Api(value = "树形字典管理", tags = "树形字典管理")
@RestController
@RequestMapping("tree")
public class SysTreeController {

    final SysTreeService sysTreeService;

    @Log
    @ApiOperation(value = "查询所有树形字典", notes = "不分页")
    @GetMapping("/list")
    public R<List<Tree<Object>>> treeList(@RequestParam(required = false) String name) {
        List<SysTree> list = sysTreeService.list(Wrappers.<SysTree>lambdaQuery()
                .like(StringUtils.isNotBlank(name), SysTree::getName, name));
        List<TreePo> treePos = list.stream()
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());
        List<Tree<Object>> tree = TreeUtils.tree(treePos, SysTree.DICT_ID_ROOT);
        return R.ok(tree);
    }

    @Log
    @ApiOperation(value = "获取指定树节点", notes = "返回指定树节点的children数组")
    @GetMapping("/get/{id}")
    public R<List<Tree<Object>>> getById(@PathVariable("id") String id) {
        SysTree sysTree = sysTreeService.getById(id);
        if (Objects.isNull(sysTree)) {
            return R.ok(Collections.emptyList());
        }
        String groupId = sysTree.getGroupId();
        List<SysTree> sysTrees = sysTreeService.list(Wrappers.<SysTree>lambdaQuery().eq(SysTree::getGroupId, groupId));
        List<TreePo> treePos = sysTrees.stream()
                .map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());
        List<Tree<Object>> tree = TreeUtils.tree(treePos, SysTree.DICT_ID_ROOT);
        List<Tree<Object>> children = tree.get(0).getChildren();
        return R.ok(children);
    }

    /**
     * 树形字典新增节点
     * <p>
     * 1. 顶级节点的上级id默认为-1
     * 2. 同一层级的字典名称不能重复
     *
     * @param dto 字典数据
     * @return 新增后的字典数据
     */
    @Log
    @ApiOperation(value = "树形字典新增节点")
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<SysTree> save(@RequestBody SysTreeSaveDto dto) {
        SysTree tree = BeanCopyUtil.copy(dto, SysTree.class);
        String name = tree.getName();
        String parentId = tree.getParentId();
        // 字典名称校验
        if (StrUtil.isBlank(name)) {
            return R.failed("字典名称不能为空");
        }
        String idStr = IdUtil.getSnowflake().nextIdStr();
        if (StringUtils.isBlank(parentId) || SysTree.DICT_ID_ROOT.equals(parentId)) {
            // 根节点
            parentId = SysTree.DICT_ID_ROOT;
            tree.setSort(0);
            tree.setGroupId(idStr);
            tree.setUniqueName(idStr);
            tree.setParentId(SysTree.DICT_ID_ROOT);
        } else {
            // 子节点
            SysTree parent = sysTreeService.getById(parentId);
            if (Objects.isNull(parent)) {
                log.error("[树形字典] 新增失败, 上级节点不存在, id: {}", parentId);
                return R.failed("上级节点不存在");
            }
            int count = sysTreeService.count(Wrappers.<SysTree>lambdaQuery().eq(SysTree::getGroupId, parent.getGroupId()));
            tree.setSort(count);
            tree.setUniqueName(idStr);
            tree.setGroupId(parent.getGroupId());
        }
        sysTreeService.checkName(name, parentId);
        sysTreeService.save(tree);
        return R.ok(tree);
    }

    /**
     * 修改树形字典
     * <p>
     * 1. 层级关系不允许修改
     * 2. 同一层级的字典名称不能重复
     *
     * @param dto 树形字典
     * @return 修改结果
     */
    @Log
    @ApiOperation(value = "修改树形字典")
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> update(@RequestBody SysTreeSaveDto dto) {
        // 层级关系不允许修改
        dto.setParentId(null);
        SysTree tree = BeanCopyUtil.copy(dto, SysTree.class);
        String id = tree.getId();
        String name = tree.getName();
        if (Objects.isNull(id)) {
            log.error("[树形字典] 修改失败, id为空");
            return R.failed("该字典不存在");
        }
        SysTree oldTree = sysTreeService.getById(id);
        if (Objects.isNull(oldTree)) {
            log.error("[树形字典] 修改失败, 字典不存在, id: {}", id);
            return R.failed("该字典不存在");
        }
        if (StringUtils.isNotBlank(name) && !name.equals(oldTree.getName())) {
            // 校验字典名称
            sysTreeService.checkName(name, oldTree.getParentId());
        }
        sysTreeService.updateById(tree);
        return R.ok(true, "修改成功");
    }

    @Log
    @ApiOperation(value = "删除树形字典")
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> delete(@PathVariable("id") String id) {
        sysTreeService.deleteTree(id);
        return R.ok(true, "删除成功");
    }

}
