package cn.bctools.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.SysTree;
import cn.bctools.auth.mapper.SysTreeMapper;
import cn.bctools.auth.service.SysTreeService;
import cn.bctools.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : GaoZeXi
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysTreeImpl extends ServiceImpl<SysTreeMapper, SysTree> implements SysTreeService {

    /**
     * 删除分类以及子集
     *
     * @param id 字典id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTree(String id) {
        SysTree sysTree = this.getById(id);
        String parentId = sysTree.getParentId();
        String groupId = sysTree.getGroupId();
        // 删除当前节点以及子节点
        if (SysTree.DICT_ID_ROOT.equals(parentId)) {
            this.remove(Wrappers.<SysTree>lambdaQuery().eq(SysTree::getGroupId, groupId));
        } else {
            List<String> childIds = this.getChild(id, null);
            this.removeByIds(childIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkName(String name, String parentId) {
        int count = this.count(Wrappers.<SysTree>lambdaQuery()
                .eq(SysTree::getName, name)
                .eq(SysTree::getParentId, parentId));
        if (count > 0) {
            log.error("[树形字典] 同一层级的字典名称不能重复, name: {}, id: {}", name, parentId);
            throw new BusinessException("同一层级的字典名称不能重复: {}", name);
        }
    }

    /**
     * 递归查询所有的子集
     *
     * @param id  当前节点id
     * @param ids 子节点id集合
     */
    private List<String> getChild(String id, List<String> ids) {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        if (id == null) {
            return null;
        }
        ids.add(id);
        List<SysTree> list = list(Wrappers.query(new SysTree().setParentId(id)));
        if (ObjectUtil.isNotEmpty(list)) {
            for (SysTree sysTree : list) {
                String sysTreeId = sysTree.getId();
                getChild(sysTreeId, ids);
            }
        }
        return ids;
    }

}
