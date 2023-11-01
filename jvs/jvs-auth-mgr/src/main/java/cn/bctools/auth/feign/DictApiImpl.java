package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.DictApi;
import cn.bctools.auth.api.dto.SysDictDto;
import cn.bctools.auth.api.dto.SysDictItemDto;
import cn.bctools.auth.controller.platform.DictController;
import cn.bctools.auth.entity.SysDict;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.service.SysDictItemService;
import cn.bctools.auth.service.SysDictService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.DozerUtil;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = "字典管理")
@RequestMapping
@RestController
@AllArgsConstructor
public class DictApiImpl implements DictApi {

    DictController dictController;
    SysDictService sysDictService;
    SysDictItemService sysDictItemService;

    @Log
    @Override
    @ApiOperation("通过ID查询字典信息")
    public R<SysDict> getById(@PathVariable("id") String id) {
        return dictController.getById(id);
    }

    @Log
    @Override
    @ApiOperation("分页查询字典信息")
    public R<IPage<SysDict>> getDictPage(Integer current, Integer size, SysDictDto sysDict) {
        SysDict dict = BeanCopyUtil.copy(sysDict, SysDict.class);
        return dictController.getDictPage(new Page<>(current, size), dict);
    }

    @Log
    @Override
    @ApiOperation("通过字典类型查找字典")
    public R<List<SysDictItemDto>> getDictByType(@PathVariable("type") String type) {
        List<SysDictItem> list = dictController.getDictByType(type).getData();
        return R.ok(DozerUtil.mapList(list, SysDictItemDto.class));
    }

    @Log
    @Override
    @ApiOperation("添加字典")
    @Transactional(rollbackFor = Exception.class)
    public R<String> save(@Valid @RequestBody SysDictDto sysDict) {
        SysDict dict = BeanCopyUtil.copy(sysDict, SysDict.class);
        return dictController.save(dict);
    }

    @Log
    @Override
    @ApiOperation("删除字典")
    public R<Boolean> removeById(@PathVariable("id") String id) {
        return dictController.removeById(id);
    }

    @Log
    @Override
    @ApiOperation("修改字典")
    public R<Boolean> updateById(@Valid @RequestBody SysDictDto sysDict) {
        SysDict copy = BeanCopyUtil.copy(sysDict, SysDict.class);
        return dictController.updateById(copy);
    }

    @Log
    @Override
    @ApiOperation("通过id查询字典项")
    public R<List<SysDictItemDto>> getDictItemById(@PathVariable("dictId") String dictId) {
        List<SysDictItem> list = dictController.getDictItemById(dictId).getData();
        return R.ok(BeanCopyUtil.copys(list, SysDictItemDto.class));
    }

    @Log
    @Override
    @ApiOperation("新增或修改字典项")
    public R save(@RequestBody List<SysDictItemDto> dictItemDtoList, @PathVariable("id") String id) {
        List<SysDictItem> items = BeanCopyUtil.copys(dictItemDtoList, SysDictItem.class);
        return dictController.save(items, id);
    }

    @Log
    @Override
    @ApiOperation("多种字典类型的字典列表")
    public R<Map<String, List<SysDictDto>>> getMultipleTypesOfDict(@RequestParam("types") List<String> types) {
        return dictController.getMultipleTypesOfDict(types);
    }

    @Log
    @Override
    @ApiOperation("所有字典")
    public R<List<SysDictDto>> list(String description) {
        LambdaQueryWrapper<SysDict> wrapper = Wrappers.lambdaQuery();
        String val = StrUtil.trimToEmpty(description);
        wrapper.like(StrUtil.isNotBlank(val), SysDict::getDescription, val);
        List<SysDict> list = sysDictService.list(wrapper.select(SysDict::getUniqId, SysDict::getDescription));
        return R.ok(BeanCopyUtil.copys(list, SysDictDto.class));
    }

    @Log
    @Override
    @ApiOperation("所有字典项")
    public R<List<SysDictItemDto>> listItems(String uniqId) {
        SysDict dict = sysDictService.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUniqId, uniqId).last("LIMIT 1"));
        if (null == dict) {
            return R.ok(Collections.emptyList());
        }
        List<SysDictItem> list = sysDictItemService.list(Wrappers.<SysDictItem>lambdaQuery()
                .eq(SysDictItem::getDictId, dict.getId())
                .orderByAsc(SysDictItem::getSort));
        return R.ok(BeanCopyUtil.copys(list, SysDictItemDto.class));
    }

}
