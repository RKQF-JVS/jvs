package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.common.utils.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息查询
 *
 * @author: GuoZi
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "dept")
public interface AuthDeptServiceApi {

    String PREFIX = "/api/dept";

    /**
     * 查询单个部门信息
     *
     * @param deptId 部门id
     * @return 部门信息
     */
    @GetMapping(PREFIX + "/dept/{deptId}")
    R<SysDeptDto> get(@ApiParam("部门id") @PathVariable("deptId") String deptId);

    /**
     * 根据部门id集合获取部门信息集合
     *
     * @param ids 部门id集合
     * @return 部门信息集合
     */
    @PostMapping(PREFIX + "/dept/listIdName")
    R<List<SysDeptDto>> list(@RequestBody List<String> ids);

    /**
     * 查询所有部门信息
     *
     * @return 部门信息(树结构)
     */
    @GetMapping(PREFIX + "/dept/query/all/tree")
    R<List<SysDeptDto>> queryAllByTree();

    /**
     * 查询下级部门信息
     *
     * @param deptId 部门id
     * @return 下级部门信息集合
     */
    @GetMapping(PREFIX + "/dept/query/child/{deptId}")
    R<List<SysDeptDto>> queryChildDepts(@ApiParam("部门id") @PathVariable("deptId") String deptId);

    /**
     * 查询上级部门信息
     *
     * @param deptId 部门id
     * @return 上级部门信息集合
     */
    @GetMapping(PREFIX + "/dept/query/parent/{deptId}")
    R<SysDeptDto> queryParentDept(@ApiParam("部门id") @PathVariable("deptId") String deptId);


    /**
     * 删除部门及其子部门
     * <p>
     * 部门以及子部门里面有用户时无法删除
     *
     * @param id 部门id
     * @return 删除结果
     */
    @DeleteMapping(PREFIX + "/dept/{id}")
    R delete(@PathVariable("id") String id);

}
