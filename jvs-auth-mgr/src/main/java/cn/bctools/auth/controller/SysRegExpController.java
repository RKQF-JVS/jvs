package cn.bctools.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.SysRegExp;
import cn.bctools.auth.service.SysRegExpService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author : GaoZeXi
 */
@Slf4j
@AllArgsConstructor
@Api(value = "正则字典管理", tags = "正则字典管理")
@RestController
@RequestMapping("/regexp")
public class SysRegExpController {

    SysRegExpService sysRegExpService;

    @Log
    @ApiOperation(value = "分页查询正则")
    @GetMapping("/list")
    public R<Page<SysRegExp>> list(Page<SysRegExp> page, SysRegExp sysRegExp) {
        String name = sysRegExp.getName();
        String type = sysRegExp.getType();
        sysRegExpService.page(page, Wrappers.<SysRegExp>lambdaQuery()
                .eq(StringUtils.isNotBlank(type), SysRegExp::getType, type)
                .like(StringUtils.isNotBlank(name), SysRegExp::getName, name));
        return R.ok(page);
    }

    @Log
    @ApiOperation(value = "查询分类")
    @GetMapping("/types")
    public R<Set<String>> types() {
        List<SysRegExp> expList = sysRegExpService.list(Wrappers.<SysRegExp>lambdaQuery().select(SysRegExp::getType));
        Set<String> typeSet = expList.stream().map(SysRegExp::getType).filter(Objects::nonNull).collect(Collectors.toSet());
        return R.ok(typeSet);
    }

    @ApiOperation(value = "新增正则", notes = "名称不允许重复")
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<SysRegExp> addRegExp(@RequestBody @Validated SysRegExp sysRegExp) {
        String name = sysRegExp.getName();
        int count = sysRegExpService.count(Wrappers.<SysRegExp>lambdaQuery().eq(SysRegExp::getName, name));
        if (count > 0) {
            return R.failed("新增失败, 名称重复:" + name);
        }
        log.info("新增正则: {}", JSONObject.toJSONString(sysRegExp));
        sysRegExp.setUniqueName(UUID.randomUUID().toString());
        sysRegExpService.save(sysRegExp);
        return R.ok(sysRegExp);
    }

    @ApiOperation(value = "修改正则")
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public R<?> updateRegexp(@RequestBody @Validated SysRegExp sysRegExp) {
        if (StringUtils.isBlank(sysRegExp.getId())) {
            return R.failed("必选参数缺失,id");
        }
        // 不允许修改该字段
        sysRegExp.setUniqueName(null);
        sysRegExpService.updateById(sysRegExp);
        String name = sysRegExp.getName();
        int count = sysRegExpService.count(Wrappers.<SysRegExp>lambdaQuery().eq(SysRegExp::getName, name));
        if (count > 1) {
            return R.failed("修改失败, 名称重复:" + name);
        }
        return R.ok("修改成功");
    }

    @ApiOperation(value = "删除正则")
    @DeleteMapping("/{id}")
    public R<?> deleteById(@PathVariable("id") String id) {
        sysRegExpService.removeById(id);
        return R.ok("删除成功");
    }

}
