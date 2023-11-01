package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.common.utils.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
     * 查询所有角色
     *
     * @return 角色信息集合
     */
    @GetMapping(PREFIX + "/query/all")
    R<List<SysRoleDto>> getAll();

    /**
     * 根据角色id查询单个角色
     *
     * @param roleId 角色id
     * @return 角色信息
     */
    @GetMapping(PREFIX + "/query/by/id/{roleId}")
    R<SysRoleDto> getById(@ApiParam("角色id") @PathVariable("roleId") String roleId);

    /**
     * 根据角色id集合查询角色集合
     * <p>
     * 返回集合的数量、顺序不能保证与入参一致
     *
     * @param roleIds 角色id集合
     * @return 角色信息集合
     */
    @PostMapping(PREFIX + "/query/by/ids")
    R<List<SysRoleDto>> getByIds(@ApiParam("角色id集合") @RequestBody List<String> roleIds);

}
