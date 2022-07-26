package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 角色信息查询
 *
 * @author: GuoZi
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "role")
public interface AuthRoleServiceApi {

    String PREFIX = "/api/role";

    /**
     * 查询所有角色信息
     *
     * @return 角色信息集合
     */
    @GetMapping(PREFIX + "/query/all")
    R<List<SysRoleDto>> queryAll();

    /**
     * 根据角色id获取角色详情
     *
     * @param id 角色id
     * @return 详情
     */
    @GetMapping(PREFIX + "/{id}")
    R<SysRoleDto> getById(@PathVariable("id") String id);

    /**
     * 根据角色id集合获取角色信息
     *
     * @param ids 角色id集
     * @return 角色集合
     */
    @GetMapping(PREFIX + "/infos")
    R<List<SysRoleDto>> getByIds(@RequestParam("ids") List<String> ids);

}
