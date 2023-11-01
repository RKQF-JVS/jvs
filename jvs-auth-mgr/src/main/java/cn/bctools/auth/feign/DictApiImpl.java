package cn.bctools.auth.feign;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.DozerUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.api.api.DictApi;
import cn.bctools.auth.api.dto.SysDictDto;
import cn.bctools.auth.api.dto.SysDictItemDto;
import cn.bctools.auth.entity.SysDict;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.service.SysDictItemService;
import cn.bctools.auth.service.SysDictService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Api(tags = "字典管理")
@RequestMapping
@RestController
@AllArgsConstructor
public class DictApiImpl implements DictApi {

    private final SysDictService sysDictService;
    private final SysDictItemService sysDictItemService;

    @Log
    @Override
    @ApiOperation("通过ID查询字典信息")
    public R<SysDict> getById(@PathVariable("id") String id) {
        return R.ok(sysDictService.getById(id));
    }

    @Log
    @Override
    @ApiOperation("分页查询字典信息")
    public R<IPage<SysDict>> getDictPage(Integer current, Integer size, SysDictDto sysDict) {
        return R.ok(sysDictService.page(new Page<>(current, size), Wrappers.query(BeanCopyUtil.copy(sysDict, SysDict.class))));
    }

    @Log
    @Override
    @ApiOperation("通过字典类型查找字典")
    public R<List<SysDictItemDto>> getDictByType(@PathVariable("type") String type) {
        List<SysDictItem> list = sysDictService.getDictByType(type);
        return R.ok(DozerUtil.mapList(list, SysDictItemDto.class));
    }

    @Log
    @Override
    @ApiOperation("添加字典")
    @Transactional(rollbackFor = Exception.class)
    public R save(@Valid @RequestBody SysDictDto sysDict) {
        SysDict copy = BeanCopyUtil.copy(sysDict, SysDict.class);
        copy.setCreateBy(UserCurrentUtils.getAccountName());
        copy.setUniqId(IdUtil.fastSimpleUUID());
        return R.ok(sysDictService.saveDict(copy));
    }

    @Log
    @Override
    @ApiOperation("删除字典")
    public R removeById(@PathVariable("id") String id) {
        sysDictService.removeDict(id);
        return R.ok();
    }

    @Log
    @Override
    @ApiOperation("修改字典")
    public R updateById(@Valid @RequestBody SysDictDto sysDict) {
        SysDict copy = BeanCopyUtil.copy(sysDict, SysDict.class);
        sysDictService.updateDict(copy);
        return R.ok();
    }

    @Log
    @Override
    @ApiOperation("通过id查询字典项")
    public R<List<SysDictItemDto>> getDictItemById(@PathVariable("id") String id) {
        List<SysDictItem> list = sysDictItemService.list(Wrappers.query(new SysDictItem().setDictId(id)));
        return R.ok(BeanCopyUtil.copys(list, SysDictItemDto.class));
    }

    @Log
    @Override
    @ApiOperation("新增或修改字典项")
    public R save(@RequestBody List<SysDictItemDto> sysDictItem, @PathVariable("id") String id) {
        //校验数据值是否有重复的
        Set<String> set = Optional.ofNullable(sysDictItem).orElse(Collections.emptyList()).stream().filter(f -> StrUtil.isNotBlank(f.getValue())).map(SysDictItemDto::getValue).collect(Collectors.toSet());
        if (ObjectUtils.isEmpty(sysDictItem) || ObjectUtil.isEmpty(set) || set.size() != sysDictItem.size()) {
            return R.failed("数据值不能为空且不能有重复的");
        }
        SysDict dict = Optional.ofNullable(sysDictService.getById(id)).orElseThrow(() -> new BusinessException("字典不存在"));
        List<String> ids = sysDictItemService.list(Wrappers.query(new SysDictItem().setDictId(id)))
                .stream()
                .map(SysDictItem::getId)
                .collect(Collectors.toList());
        //排除已经有的
        List<String> collect = sysDictItem.stream()
                .map(e -> e.setDictId(id))
                .map(e -> e.setType(dict.getType()))
                .map(SysDictItemDto::getId).collect(Collectors.toList());
        ids.removeAll(collect);
        //删除多余的字段项
        sysDictItemService.removeByIds(ids);
        //保存或更新新的
        sysDictItemService.saveOrUpdateBatch(BeanCopyUtil.copys(sysDictItem, SysDictItem.class));
        return R.ok();
    }

    @Log
    @Override
    @ApiOperation("多种字典类型的字典列表")
    public R<Map<String, List<SysDictDto>>> getMultipleTypesOfDict(@RequestParam("types") List<String> types) {
        Map<String, List<SysDictDto>> map = sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().in(SysDictItem::getType, types))
                .stream()
                .map(e -> BeanCopyUtil.copy(e, SysDictDto.class))
                .collect(Collectors.groupingBy(SysDictDto::getType));
        return R.ok(map);
    }

    @Log
    @Override
    @ApiOperation("所有字典")
    public R<List<SysDictDto>> list(String description) {
        LambdaQueryWrapper<SysDict> lambdaQuery = Wrappers.lambdaQuery();
        String val = StrUtil.trimToEmpty(description);
        lambdaQuery.like(StrUtil.isNotBlank(val), SysDict::getDescription, val);
        List<SysDict> list = sysDictService.list(lambdaQuery.select(SysDict::getUniqId, SysDict::getDescription));
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
        List<SysDictItem> list = sysDictItemService.list(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, dict.getId()));
        return R.ok(BeanCopyUtil.copys(list, SysDictItemDto.class));
    }
}
