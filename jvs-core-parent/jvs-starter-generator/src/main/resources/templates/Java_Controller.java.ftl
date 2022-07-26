package ${rootPkg}.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.common.utils.R;
import ${rootPkg}.entity.${entityName};
import ${rootPkg}.service.${entityName}Service;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ${tableInfo}
 * <p>
 * FIXME 需要注意，该类为自动生成，需结合真实业务进行修改
 *
 * @author Auto Generator
 */
@Api(tags = "${tableInfo}")
@RestController
@AllArgsConstructor
@RequestMapping("/${entityName}")
public class ${entityName}Controller {

    ${entityName}Service service;

    @Log
    @ApiOperation("分页")
    @GetMapping("/page")
    public R<${r"Page"}<${entityName}>> page(Page<${entityName}> page, ${entityName} dto) {
        service.page(page, Wrappers.lambdaQuery(dto));
        return R.ok(page);
    }

    @Log
    @ApiOperation("详情")
    @GetMapping("/detail")
    public R<${entityName}> detail(${entityName} dto) {
        return R.ok(service.getOne(Wrappers.lambdaQuery(dto)));
    }

    @Log
    @ApiOperation("新增")
    @PostMapping("/save")
    public R<${entityName}> save(@RequestBody ${entityName} dto) {
        service.save(dto);
        return R.ok(dto);
    }

    @Log
    @ApiOperation("修改")
    @PutMapping("/edit")
    public R<${entityName}> edit(@RequestBody ${entityName} dto) {
        service.updateById(dto);
        return R.ok(dto);
    }

    @Log
    @ApiOperation("删除")
    @DeleteMapping("/del")
    public R<${r"Boolean"}> remove(${entityName} dto) {
        return R.ok(service.removeByMap(BeanUtil.beanToMap(dto,true,true)));
    }

}
