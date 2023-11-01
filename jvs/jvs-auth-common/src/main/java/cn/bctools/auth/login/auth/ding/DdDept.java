package cn.bctools.auth.login.auth.ding;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.enums.AuthSourceEnum;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.common.exception.BusinessException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2DepartmentGetRequest;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ZhuXiaoKang
 * @Description: 钉钉部门
 */
@Slf4j
@Component
public class DdDept {
    // 如果企业开通了家校通讯录，获取根部门下的一级子部门内有家校通讯录，部门ID值为-7，需要把该部门剔除，不属于内部通讯录范畴。
    private static final Long EXCLUDE_DEPT_ID = -7L;
    // 根部门id
    public static final Long ROOT_PARENT_ID = 1L;
    private static final String GET_DEPARTMENT_ALL_URL = "https://oapi.dingtalk.com/topapi/v2/department/listsub";
    private static final String GET_DEPARTMENT_DETAIL_URL = "https://oapi.dingtalk.com/topapi/v2/department/get";

    /**
     * 钉钉未提供直接获取所有部门的接口。
     * 获取所有部门的步骤：
     * 1. 先获取所有根部门
     * 2. 循环获取每一个部门下的部门列表，直到每一个部门ID作为参数时获取的结果为空，就可以获取到企业所有的部门信息。
     * @return
     */
    public List<Dept> getDeptAll(String accessToken) {
        List<Dept> treePos = new ArrayList<>();
        iteratorDept(accessToken, ROOT_PARENT_ID, treePos);
        return treePos;
    }

    /**
     * 递归获取所有部门
     * @param accessToken
     * @param deptId  部门id
     * @param deptPos 部门信息集合
     */
    private void iteratorDept(String accessToken, Long deptId, List<Dept> deptPos) {
        String parentId = ROOT_PARENT_ID.equals(deptId) ? AuthConfigUtil.tenantId(SysConfigTypeEnum.DING_H5) : deptId.toString();
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> depts = getDeptChilds(accessToken, deptId);
        if (CollectionUtils.isNotEmpty(depts)) {
            depts = depts.stream().filter(d -> Boolean.FALSE.equals(EXCLUDE_DEPT_ID.equals(d.getDeptId()))).collect(Collectors.toList());
            depts.parallelStream().forEach(d -> {
                OapiV2DepartmentGetResponse.DeptGetResponse dept = getDept(accessToken, d.getDeptId());
                deptPos.add(new Dept().setId(dept.getDeptId().toString()).setName(dept.getName()).setSort(dept.getOrder().intValue()).setParentId(parentId).setSource(AuthSourceEnum.DINGTALK_INSIDE));
                iteratorDept(accessToken, dept.getDeptId(), deptPos);
            });
        }
    }

    /**
     * 获取指定部门的子部门
     * @param accessToken
     * @param deptId 部门id
     * @return
     */
    @SneakyThrows
    private List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptChilds(String accessToken, Long deptId) {
        DingTalkClient client = new DefaultDingTalkClient(GET_DEPARTMENT_ALL_URL);
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(deptId);
        req.setLanguage("zh_CN");
        OapiV2DepartmentListsubResponse rsp = client.execute(req, accessToken);
        if (DdBase.DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("获取钉钉部门列表失败：{}", rsp.getBody());
            throw new BusinessException("获取钉钉部门列表失败");
        }
        return rsp.getResult();
    }

    /**
     * 获取部门详情
     * @param accessToken
     * @param deptId 部门id
     */
    @SneakyThrows
    public OapiV2DepartmentGetResponse.DeptGetResponse getDept(String accessToken, Long deptId) {
        DingTalkClient client = new DefaultDingTalkClient(GET_DEPARTMENT_DETAIL_URL);
        OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
        req.setDeptId(deptId);
        req.setLanguage("zh_CN");
        OapiV2DepartmentGetResponse rsp = client.execute(req, accessToken);
        if (DdBase.DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("获取钉钉部门详情失败：{}", rsp.getBody());
            throw new BusinessException("获取钉钉部门详情失败");
        }
        return rsp.getResult();
    }
}
