package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SearchDto;
import cn.bctools.auth.api.dto.UserSelectedDto;
import cn.bctools.common.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
 * @author : GaoZeXi
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "selected")
public interface SelectedServiceApi {

    String PREFIX = "/api/selected";

    /**
     * 查询选择对象数据集
     *
     * @param searchDto 搜索
     * @return 正则字典集合
     */
    @ApiOperation(value = "查询选择对象数据集")
    @PostMapping(PREFIX + "/search")
    R<UserSelectedDto> search(@RequestBody SearchDto searchDto);


}
