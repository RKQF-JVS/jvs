package cn.bctools.auth.feign;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.api.api.TreeServiceApi;
import cn.bctools.auth.api.dto.SysTreeDto;
import cn.bctools.auth.entity.SysTree;
import cn.bctools.auth.service.SysTreeService;
import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TreeUtils;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : GaoZeXi
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class TreeApiImpl implements TreeServiceApi {

    final SysTreeService sysTreeService;

    @Log
    @Override
    @ApiOperation("查询所有分类字典")
    public R<List<SysTreeDto>> list(String name) {
        List<SysTree> list = sysTreeService.list(Wrappers.<SysTree>lambdaQuery()
                .like(StringUtils.isNotBlank(name), SysTree::getName, name)
                .eq(SysTree::getParentId, SysTree.DICT_ID_ROOT));
        List<SysTreeDto> result = BeanCopyUtil.copys(list, SysTreeDto.class);
        return R.ok(result);
    }

    @Log
    @Override
    @ApiOperation("根据字典标识查询字典树")
    @Transactional(rollbackFor = Exception.class)
    public R<Tree<Object>> getByUniqueName(String uniqueName) {
        SysTree tree = sysTreeService.getOne(Wrappers.<SysTree>lambdaQuery().eq(SysTree::getUniqueName, uniqueName));
        if (Objects.isNull(tree)) {
            // 不返回failed
            return R.ok();
        }
        String groupId = tree.getGroupId();
        List<SysTree> treeList = sysTreeService.list(Wrappers.<SysTree>lambdaQuery().eq(SysTree::getGroupId, groupId));
        List<TreePo> treePos = treeList
                .stream().map(e -> BeanCopyUtil.copy(e, TreePo.class).setExtend(e))
                .collect(Collectors.toList());
        List<Tree<Object>> result = TreeUtils.tree(treePos, SysTree.DICT_ID_ROOT);
        return R.ok(result.get(0));
    }

    @Log
    @Override
    @ApiOperation("根据字典标识集合查询字典数据")
    public R<Map<String, SysTreeDto>> getByUniqueNames(List<String> uniqueNames) {
        if(ObjectUtil.isEmpty(uniqueNames)){
            return R.ok();
        }
        List<SysTree> list = sysTreeService.list(Wrappers.<SysTree>lambdaQuery().in(SysTree::getUniqueName, uniqueNames));
        if(ObjectUtil.isEmpty(list)){
            return R.ok();
        }
        List<SysTreeDto> treeList = BeanCopyUtil.copys(list, SysTreeDto.class);
        Map<String, SysTreeDto> treeMap = treeList.stream().collect(Collectors.toMap(SysTreeDto::getUniqueName, Function.identity()));
        return R.ok(treeMap);
    }

}
