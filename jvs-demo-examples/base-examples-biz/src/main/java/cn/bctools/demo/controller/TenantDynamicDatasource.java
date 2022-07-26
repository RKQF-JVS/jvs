package cn.bctools.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.common.utils.R;
import cn.bctools.demo.entity.Demo;
import cn.bctools.demo.mapper.DemoMapper;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@RequestMapping("/test")
@RestController
@AllArgsConstructor
@Api(tags = "多租户动态回调")
public class TenantDynamicDatasource {

    DemoMapper demoMapper;

    @SneakyThrows
    @GetMapping()
    public R index(String tenantId) {
        List<Demo> demos = demoMapper.selectList(Wrappers.lambdaQuery());
        return R.ok(demos);
    }

}
