package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.common.utils.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
     * 查询所有部门
     *
     * @return 部门信息集合(单列集合结构)
     */
    @GetMapping(PREFIX + "/query/all")
    R<List<SysDeptDto>> getAll();

    /**
     * 查询部门id查询单个部门信息
     *
     * @param deptId 部门id
     * @return 部门信息
     */
    @GetMapping(PREFIX + "/query/by/id/{deptId}")
    R<SysDeptDto> getById(@ApiParam("部门id") @PathVariable("deptId") String deptId);

    /**
     * 根据部门id集合获取部门信息集合
     * <p>
     * 返回集合的数量、顺序不能保证与入参一致
     *
     * @param deptIds 部门id集合
     * @return 部门信息集合
     */
    @PostMapping(PREFIX + "/query/by/ids")
    R<List<SysDeptDto>> getByIds(@ApiParam("部门id集合") @RequestBody List<String> deptIds);

    /**
     * 查询所有部门信息
     *
     * @return 部门信息(树结构)
     */
    @GetMapping(PREFIX + "/query/all/tree")
    R<List<SysDeptDto>> getAllTree();

    /**
     * 查询直接下级部门信息
     *
     * @param deptId 部门id
     * @return 下级部门信息集合
     */
    @GetMapping(PREFIX + "/query/child/{deptId}")
    R<List<SysDeptDto>> getChildList(@ApiParam("部门id") @PathVariable("deptId") String deptId);

    /**
     * 查询直接上级部门信息
     * <p>
     * 若当前部门已经是顶级部门, 则返回当前部门信息
     *
     * @param deptId 部门id
     * @return 上级部门信息集合
     */
    @GetMapping(PREFIX + "/query/parent/{deptId}")
    R<SysDeptDto> getParent(@ApiParam("部门id") @PathVariable("deptId") String deptId);

    /**
     * 这个部门所在的上级公司
     */
    @GetMapping(PREFIX + "/query/parentBranchOffice/{deptId}")
    R<SysDeptDto> getParentBranchOffice(@ApiParam("部门id") @PathVariable("deptId") String deptId);
}
