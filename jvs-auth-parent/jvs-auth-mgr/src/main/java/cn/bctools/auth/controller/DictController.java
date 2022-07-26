package cn.bctools.auth.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.api.dto.SysDictDto;
import cn.bctools.auth.entity.SysDict;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.service.SysDictItemService;
import cn.bctools.auth.service.SysDictService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典管理
 *
 * @author guojing
 */
@Api(tags = "字典管理")
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
public class DictController {

    private final SysDictService sysDictService;

    private final SysDictItemService sysDictItemService;

    /**
     * 通过ID查询字典信息
     *
     * @param id ID
     * @return 字典信息
     */
    @GetMapping("/{id}")
    public R<SysDict> getById(@PathVariable String id) {
        return R.ok(sysDictService.getById(id));
    }

    @Log
    @ApiOperation("分页查询字典信息")
    @GetMapping("/page")
    public R<IPage<SysDict>> getDictPage(Page<SysDict> page, SysDict sysDict) {
        // 添加排序
        return R.ok(sysDictService.page(page, Wrappers.lambdaQuery(sysDict).orderByDesc(SysDict::getCreateTime)));
    }

    @Log
    @ApiOperation("通过字典类型查找字典")
    @GetMapping("/type/{type}")
    public R<List<SysDictItem>> getDictByType(@PathVariable String type) {
        List<SysDictItem> list = sysDictService.getDictByType(type);
        return R.ok(list);
    }

    @Log
    @ApiOperation("添加字典")
    @PostMapping
    public R<String> save(@Valid @RequestBody SysDict sysDict) {
        sysDict.setCreateBy(UserCurrentUtils.getRealName());
        sysDict.setUniqId(IdUtil.fastSimpleUUID());
        return R.ok(sysDictService.saveDict(sysDict));
    }

    @Log
    @ApiOperation("删除字典")
    @DeleteMapping("/{id}")
    public R<Boolean> removeById(@PathVariable String id) {
        sysDictService.removeDict(id);
        return R.ok(true, "删除成功");
    }

    @Log
    @ApiOperation("修改字典")
    @PutMapping
    public R<Boolean> updateById(@Valid @RequestBody SysDict sysDict) {
        sysDictService.updateDict(sysDict);
        return R.ok(true, "修改成功");
    }

    @Log
    @ApiOperation("通过id查询字典项")
    @GetMapping("/item/{id}")
    public R<List<SysDictItem>> getDictItemById(@PathVariable("id") String id) {
        List<SysDictItem> list = sysDictItemService.list(Wrappers.query(new SysDictItem().setDictId(id)));
        return R.ok(list);
    }

    @Log
    @ApiOperation("新增或修改字典项")
    @PostMapping("/item/{id}")
    public R<Boolean> save(@RequestBody List<SysDictItem> sysDictItem, @PathVariable String id) {
        SysDict dict = sysDictService.getById(id);
        List<String> ids = sysDictItemService.list(Wrappers.query(new SysDictItem().setDictId(id)))
                .stream()
                .map(SysDictItem::getId)
                .collect(Collectors.toList());
        //排除已经有的
        List<String> collect = sysDictItem.stream()
                .map(e -> e.setDictId(id))
                .map(e -> e.setType(dict.getType()))
                .map(SysDictItem::getId).collect(Collectors.toList());
        ids.removeAll(collect);
        //删除多余的字段项
        sysDictItemService.removeByIds(ids);
        //保存或更新新的
        sysDictItemService.saveOrUpdateBatch(sysDictItem);
        return R.ok(true, "操作成功");
    }

    @ApiOperation("多种字典类型的字典列表")
    @GetMapping("/types/dicts")
    public R<Map<String, List<SysDictDto>>> getMultipleTypesOfDict(@RequestParam("types") List<String> types) {
        Map<String, List<SysDictDto>> map = sysDictItemService.list(Wrappers.<SysDictItem>query().lambda().in(SysDictItem::getType, types))
                .stream()
                .map(e -> BeanCopyUtil.copy(e, SysDictDto.class))
                .collect(Collectors.groupingBy(SysDictDto::getType));
        return R.ok(map);
    }

}
