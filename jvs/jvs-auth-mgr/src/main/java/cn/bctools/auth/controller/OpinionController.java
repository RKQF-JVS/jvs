package cn.bctools.auth.controller;


import cn.bctools.auth.entity.Opinion;
import cn.bctools.auth.service.OpinionService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 
 */
@Api(tags = "意见反馈")
@RestController
@RequestMapping("/opinion")
@AllArgsConstructor
public class OpinionController {
    private final OpinionService service;

    @Log
    @ApiOperation(value = "分页")
    @GetMapping("/page")
    public R<Page<Opinion>> page(Page<Opinion> page, Opinion vo) {
        service.page(page, Wrappers.query(vo).lambda().orderByDesc(Opinion::getCreateTime));
        return R.ok(page);
    }

}
