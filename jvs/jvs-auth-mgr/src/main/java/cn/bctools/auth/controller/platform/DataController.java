package cn.bctools.auth.controller.platform;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.SysData;
import cn.bctools.auth.service.DataService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "数据权限资源标识管理")
@RestController
@RequestMapping("data")
public class DataController {

    DataService dataService;

    @Log
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public R<Page<SysData>> page(Page<SysData> page) {
        dataService.page(page);
        return R.ok(page);
    }

    @Log
    @PostMapping("/save")
    @ApiOperation(value = "新增数据资源", notes = "新增一个数据权限的资源标识。")
    public R<Boolean> saveDataRole(@RequestBody SysData data) {
        dataService.save(data);
        return R.ok(true, "新增成功");
    }

    @Log
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除一个资源标识")
    public R<Boolean> delete(@PathVariable("id") String id) {
        dataService.removeById(id);
        return R.ok(true, "删除成功");
    }

    @Log
    @PutMapping
    @ApiOperation(value = "修改一个资源标识")
    public R<Boolean> put(@RequestBody SysData sysData) {
        dataService.updateById(sysData);
        return R.ok(true, "修改成功");
    }

}
