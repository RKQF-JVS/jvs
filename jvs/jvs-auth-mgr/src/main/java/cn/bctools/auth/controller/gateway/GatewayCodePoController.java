package cn.bctools.auth.controller.gateway;

import cn.bctools.common.utils.R;
import cn.bctools.gateway.entity.GatewayCodePo;
import cn.bctools.gateway.mapper.GatewayCodeMapper;
import cn.bctools.log.annotation.Log;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * code异常码转换关系和业务类型区分表-主要用于网关返回给前端 业务返回，默认在feign标准返回给上层,代码示例为new BusinessException("这是一个测试异常")
 * <p>
 *
 * @author Auto Generator
 */
@Api(tags = "code异常码转换")
@RestController
@AllArgsConstructor
@RequestMapping("/GatewayCodePo")
public class GatewayCodePoController {

    GatewayCodeMapper service;

    @Log
    @ApiOperation("分页")
    @GetMapping("/page")
    public R<Page<GatewayCodePo>> page(Page<GatewayCodePo> page, GatewayCodePo dto) {
        service.selectPage(page, Wrappers.lambdaQuery(dto).orderByDesc(GatewayCodePo::getCode));
        return R.ok(page);
    }

    @Log
    @ApiOperation("详情")
    @GetMapping("/detail")
    public R<GatewayCodePo> detail(GatewayCodePo dto) {
        return R.ok(service.selectOne(Wrappers.lambdaQuery(dto)));
    }

    @Log
    @ApiOperation("新增")
    @PostMapping("/save")
    public R<GatewayCodePo> save(@RequestBody GatewayCodePo dto) {
        service.insert(dto);
        return R.ok(dto);
    }

    @Log
    @ApiOperation("修改")
    @PutMapping("/edit")
    public R<GatewayCodePo> edit(@RequestBody GatewayCodePo dto) {
        service.updateById(dto);
        return R.ok(dto);
    }

    @Log
    @ApiOperation("删除")
    @DeleteMapping("/del/{id}")
    public R remove(@PathVariable String id) {
        return R.ok(service.deleteById(id));
    }

    @Log
    @ApiOperation("刷新")
    @GetMapping("/refresh")
    public R refresh() {
        return R.ok();
    }

}
