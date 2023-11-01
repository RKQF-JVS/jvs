package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SysTreeDto;
import cn.bctools.common.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author : GaoZeXi
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "tree")
public interface TreeServiceApi {

    String PREFIX = "/api/tree";

    /**
     * 查询所有分类字典
     *
     * @param name 字典名称(模糊搜索)
     * @return 字典的集合
     */
    @ApiOperation(value = "查询所有分类字典")
    @GetMapping(PREFIX + "/list")
    R<List<SysTreeDto>> list(@RequestParam(name = "name", required = false) String name);

    /**
     * 根据字典标识查询字典树
     *
     * @param uniqueName 字典标识
     * @return 字典树
     */
    @ApiOperation(value = "根据分类字典名称查询字典")
    @GetMapping(PREFIX + "/query/uniqueName")
    R<Map<String, Object>> getByUniqueName(@RequestParam("uniqueName") String uniqueName);

    /**
     * 根据字典标识集合查询字典数据
     * <p>
     * 返回结果进行了分组处理
     *
     * @param uniqueNames 字典标识集合
     * @return 字典对象
     */
    @GetMapping(PREFIX + "/query/uniqueNames")
    R<Map<String, SysTreeDto>> getByUniqueNames(@RequestParam("uniqueNames") List<String> uniqueNames);

}
