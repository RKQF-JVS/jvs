package cn.bctools.auth.controller;


import cn.bctools.auth.entity.Bulletin;
import cn.bctools.auth.entity.enums.BulletinEnum;
import cn.bctools.auth.entity.enums.BulletinPublishEnum;
import cn.bctools.auth.service.BulletinService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author zhuxiaokang
 * @description 系统公告 前端控制器
 * @since 2021-11-19
 */
@Api(tags = "系统公告")
@RestController
@RequestMapping("/bulletin")
@AllArgsConstructor
public class BulletinController {
    private final BulletinService service;

    @Log
    @ApiOperation(value = "新增")
    @PostMapping
    public R<String> create(@Validated @RequestBody Bulletin vo) {
        if (LocalDateTimeUtil.between(vo.getStartTime(), vo.getEndTime(), ChronoUnit.MILLIS) <= 0) {
            throw new BusinessException("开始时间必须大于结束时间");
        }
        Bulletin bulletin = BeanUtil.copyProperties(vo, Bulletin.class);
        service.save(bulletin);
        return R.ok();
    }

    @Log
    @ApiOperation(value = "修改")
    @PutMapping
    public R<String> edit(@Validated @RequestBody Bulletin vo) {
        if (StringUtils.isBlank(vo.getId())) {
            throw new BusinessException("id不能为空");
        }
        if (LocalDateTimeUtil.between(vo.getStartTime(), vo.getEndTime(), ChronoUnit.MILLIS) <= 0) {
            throw new BusinessException("开始时间必须大于结束时间");
        }
        Bulletin bulletin = BeanUtil.copyProperties(vo, Bulletin.class);
        service.updateById(bulletin);
        return R.ok();
    }

    @Log
    @ApiOperation(value = "删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告id", required = true)
    })
    @DeleteMapping("/{id}")
    public R<String> del(@PathVariable("id") String id) {
        service.removeById(id);
        return R.ok();
    }

    @Log
    @ApiOperation(value = "分页")
    @GetMapping("/page")
    public R<Page<Bulletin>> page(Page<Bulletin> page, Bulletin vo) {
        LambdaQueryWrapper<Bulletin> query = Wrappers.<Bulletin>lambdaQuery()
                .like(StringUtils.isNotBlank(vo.getTitle()), Bulletin::getTitle, vo.getTitle())
                .ge(vo.getStartTime() != null, Bulletin::getStartTime, vo.getStartTime())
                .le(vo.getEndTime() != null, Bulletin::getEndTime, vo.getEndTime())
                .eq(vo.getPublish() != null, Bulletin::getPublish, vo.getPublish())
                .orderByDesc(Bulletin::getCreateTime);
        service.page(page, query).getRecords().forEach(e -> {
            if (e.getContentType().equals(BulletinEnum.TEXT)) {
                ((Bulletin) e).setContent(HtmlUtil.cleanHtmlTag(((Bulletin) e).getContent()).replaceAll("\n", ""));
            }
        });
        return R.ok(page);
    }

    @Log
    @ApiOperation(value = "详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告id", required = true)
    })
    @GetMapping("/{id}")
    public R<Bulletin> detail(@PathVariable("id") String id) {
        return R.ok(service.getById(id));
    }

    @Log
    @ApiOperation("发布")
    @PutMapping("/deploy/{id}")
    public R deploy(@PathVariable("id") String id) {
        Bulletin jvsApp = service.getById(id);
        jvsApp.setPublish(true);
        return R.ok(service.updateById(jvsApp));
    }

    @Log
    @ApiOperation("卸载")
    @PutMapping("/unload/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R unload(@PathVariable("id") String id) {
        Bulletin jvsApp = service.getById(id);
        jvsApp.setPublish(false);
        return R.ok(service.updateById(jvsApp));
    }


    @ApiOperation(value = "应用公告列表")
    @GetMapping("/list")
    public R<List<Bulletin>> list() {
        String appKey = UserCurrentUtils.getCurrentUser().getClientId();
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Bulletin> query = Wrappers.<Bulletin>lambdaQuery()
                .le(Bulletin::getStartTime, now)
                .ge(Bulletin::getEndTime, now)
                .eq(Bulletin::getPublish, BulletinPublishEnum.YES)
                .apply("JSON_CONTAINS(app_keys, CONCAT('\"', {0}, '\"'), '$')", appKey)
                .orderByDesc(Bulletin::getStartTime);
        return R.ok(service.list(query));
    }

}
