package cn.bctools.auth.feign;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.api.api.RegExpServiceApi;
import cn.bctools.auth.api.dto.RegExpDto;
import cn.bctools.auth.entity.SysRegExp;
import cn.bctools.auth.service.SysRegExpService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class RegExpApiImpl implements RegExpServiceApi {

    SysRegExpService sysRegExpService;

    @Log
    @Override
    @ApiOperation(value = "分页查询所有正则")
    public R<List<RegExpDto>> list(String name) {
        LambdaQueryWrapper<SysRegExp> queryWrapper = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like(SysRegExp::getName, name);
        }
        List<SysRegExp> list = sysRegExpService.list(queryWrapper);
        List<RegExpDto> copys = BeanCopyUtil.copys(list, RegExpDto.class);
        return R.ok(copys);
    }

    @Log
    @Override
    @ApiOperation(value = "根据正则名称查询字典")
    public R<RegExpDto> getByName(String uniqueName) {
        SysRegExp one = sysRegExpService.getOne(Wrappers.query(new SysRegExp().setUniqueName(uniqueName)).last("limit 1"));
        RegExpDto copy = BeanCopyUtil.copy(one, RegExpDto.class);
        return R.ok(copy);
    }

}
