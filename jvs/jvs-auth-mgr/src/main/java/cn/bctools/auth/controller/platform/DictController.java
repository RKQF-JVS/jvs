package cn.bctools.auth.controller.platform;

import cn.bctools.auth.api.enums.SysDictEnum;
import cn.bctools.auth.entity.enums.DictTypeEnum;
import cn.bctools.redis.utils.RedisUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 字典管理
 *
 * @author 
 */
@Slf4j
@Api(tags = "字典管理")
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
public class DictController {

    RedisUtils redisUtils;
    SysDictService sysDictService;
    SysDictItemService sysDictItemService;

    /**
     * 通过ID查询字典信息
     *
     * @param id ID
     * @return 字典信息
     */
    @GetMapping("/{id}")
    public R<SysDict> getById(@PathVariable("id") String id) {
        return R.ok(sysDictService.getById(id));
    }

    @Log
    @ApiOperation("分页查询字典信息")
    @GetMapping("/page")
    public R<IPage<SysDict>> getDictPage(Page<SysDict> page, SysDict sysDict) {
        String type = sysDict.getType();
        DictTypeEnum system = sysDict.getSystem();
        String description = sysDict.getDescription();
        return R.ok(sysDictService.page(page, Wrappers.<SysDict>lambdaQuery()
                .like(StringUtils.isNotBlank(type), SysDict::getType, type)
                .like(Objects.nonNull(system), SysDict::getSystem, system)
                .like(StringUtils.isNotBlank(description), SysDict::getDescription, description)
                // 默认按创建时间排序
                .orderByDesc(SysDict::getCreateTime)));
    }

    @Log
    @ApiOperation("通过字典类型查找字典")
    @GetMapping("/type/{type}")
    public R<List<SysDictItem>> getDictByType(@PathVariable("type") String type) {
        List<SysDictItem> items = sysDictItemService.getByType(type);
        return R.ok(items);
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
    public R<Boolean> removeById(@PathVariable("id") String id) {
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
    @GetMapping("/item/{dictId}")
    public R<List<SysDictItem>> getDictItemById(@PathVariable("dictId") String dictId) {
        List<SysDictItem> list = sysDictItemService.getByDictId(dictId);
        return R.ok(list);
    }

    @Log
    @ApiOperation("新增或修改字典项")
    @PostMapping("/item/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> save(@RequestBody List<SysDictItem> sysDictItem, @PathVariable("id") String id) {
        // 字典校验
        SysDict dict = sysDictService.getById(id);
        if (Objects.isNull(dict)) {
            log.error("字典项修改异常, 字典不存在, 字典id: {}", id);
            return R.failed("字典不存在");
        }
        // 清空字典项
        sysDictItemService.remove(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
        if (ObjectUtils.isEmpty(sysDictItem)) {
            return R.ok(true, "操作成功");
        }
        String type = dict.getType();
        for (int i = 0; i < sysDictItem.size(); i++) {
            SysDictItem item = sysDictItem.get(i);
            // 默认排序
            item.setSort(i);
            item.setId(null);
            item.setDictId(id);
            item.setType(type);
        }
        // 保存或更新新的
        sysDictItemService.saveBatch(sysDictItem);
        if (SysDictEnum.icon.name().equals(type)) {
            this.handleIconDict();
        }
        return R.ok(true, "操作成功");
    }

    @ApiOperation("多种字典类型的字典列表")
    @GetMapping("/types/dicts")
    public R<Map<String, List<SysDictDto>>> getMultipleTypesOfDict(@RequestParam("types") List<String> types) {
        List<SysDictItem> items = sysDictItemService.list(Wrappers.<SysDictItem>lambdaQuery()
                .in(SysDictItem::getType, types)
                .orderByAsc(SysDictItem::getSort));
        Map<String, List<SysDictDto>> map = items.stream()
                .map(e -> BeanCopyUtil.copy(e, SysDictDto.class))
                .collect(Collectors.groupingBy(SysDictDto::getType));
        return R.ok(map);
    }

    /**
     * 图标库字典的变动处理
     */
    private void handleIconDict() {
//        redisUtils.del(IconController.REDIS_KEY_ICONS);
    }

}
