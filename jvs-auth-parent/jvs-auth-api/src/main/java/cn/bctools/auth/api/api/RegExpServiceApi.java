package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.RegExpDto;
import cn.bctools.common.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @author : GaoZeXi
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "regexp")
public interface RegExpServiceApi {

    String PREFIX = "/api/regexp";

    /**
     * 查询所有正则字典
     *
     * @param name 正则名称
     * @return 正则字典集合
     */
    @ApiOperation(value = "查询所有正则字典")
    @GetMapping(PREFIX + "/list")
    R<List<RegExpDto>> list(@RequestParam(value = "name", required = false, defaultValue = "") String name);

    /**
     * 根据正则名称查询字典
     *
     * @param uniqueName 唯一名称
     * @return 正则字典对象
     */
    @ApiOperation(value = "根据正则名称查询字典")
    @GetMapping(PREFIX + "/{uniqueName}")
    R<RegExpDto> getByName(@PathVariable("uniqueName") String uniqueName);
}
