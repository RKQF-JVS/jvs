package cn.bctools.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.Apply;
import cn.bctools.auth.service.ApplyService;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.sensitive.SensitiveInfoUtils;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "终端管理")
@RestController
@RequestMapping("apply")
public class ApplyController {

    ApplyService applyService;
    final static BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Log
    @GetMapping("/all")
    @ApiOperation(value = "获取所有应用", notes = "应用的分页查询")
    public R all() {
        List<Apply> list = applyService.list(new LambdaQueryWrapper<Apply>().select(Apply::getAppKey, Apply::getName));
        return R.ok(list);
    }

    @Log
    @GetMapping("/page")
    @ApiOperation(value = "分页查询", notes = "应用的分页查询")
    public R<Page<Apply>> page(Page<Apply> page, Apply apply) {
        applyService.page(page, Wrappers.query(apply));
        List<Apply> records = page.getRecords();
        if (ObjectUtils.isNotEmpty(records)) {
            for (Apply e : records) {
                e.setAppSecret(SensitiveInfoUtils.bankCard(e.getAppSecret()));
            }
        }
        return R.ok(page);
    }

    @Log
    @ApiOperation(value = "添加应用", notes = "每一个人都可以添加一个应用，应用下面添加菜单和资源")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> save(@RequestBody @Validated Apply apply) {
        int count = applyService.count(Wrappers.<Apply>lambdaQuery().eq(Apply::getName, apply.getName()));
        if (count > 0) {
            return R.failed("应用名称已存在");
        }
        // 应用凭证加密, 默认使用凭证key
        String secret = apply.getAppSecret();
        if (StringUtils.isBlank(secret)) {
            secret = apply.getAppKey();
        }
        String encode = PASSWORD_ENCODER.encode(secret);
        apply.setAppSecret(encode);
        if (ObjectUtil.isEmpty(apply.getAuthorizedGrantTypes())) {
            apply.setAuthorizedGrantTypes(Arrays.asList("password", "refresh_token", "authorization_code", "client_credentials"));
        }
        applyService.save(apply);
        return R.ok(true, "添加成功");
    }

    @Log
    @PutMapping
    @ApiOperation(value = "修改应用", notes = "修改一个应用操作")
    @Transactional(rollbackFor = Exception.class)
    public R<Apply> update(@RequestBody @Validated Apply apply) {
        String id = apply.getId();
        if (StringUtils.isBlank(id)) {
            return R.failed("应用id为空, 修改失败");
        }
        Apply oldApply = applyService.getById(id);
        if (Objects.isNull(oldApply)) {
            log.error("应用修改失败, 应用不存在, id: {}", id);
            return R.failed("应用不存在");
        }
        oldApply.setName(apply.getName());
        oldApply.setDescribes(apply.getDescribes());
        applyService.updateById(oldApply);
        return R.ok(oldApply);
    }

    @Log
    @GetMapping("/{id}")
    @ApiOperation(value = "获取单个", notes = "获取单个应用详情")
    public R<Apply> getOne(@PathVariable String id) {
        return R.ok(applyService.getById(id));
    }

    @Log
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除应用", notes = "删除这个应用，只能删除当前租户的，其它租户的不能删除")
    public R<Boolean> delete(@PathVariable String id) {
        applyService.removeById(id);
        return R.ok(true, "删除成功");
    }

}
