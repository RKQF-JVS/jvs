package cn.bctools.auth.login.auth.wx.enterprise;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.enums.AuthSourceEnum;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.web.utils.HttpRequestUtils;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.UrlBuilder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ZhuXiaoKang
 * @Description: 企业微信部门
 */
@Slf4j
@Component
public class WxEnterpriseDept {
    private static final String DEPT_ID_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/simplelist";
    private static final String DEPT_DETAIL_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/get";
    /**
     * 根部门id为1
     */
    public static final int ROOT_PARENT_ID = 1;
    private static final String DEPARTMENT_ID = "department_id";
    private static final String DEPARTMENT = "department";
    public static final String DEPT_ID = "id";
    private static final String DEPT_NAME = "name";
    private static final String DEPT_PARENT_ID = "parentid";
    private static final String DEPT_ORDER = "order";

    /**
     * 获取部门列表
     * @param accessToken
     * @return
     */
    public List<Dept> getDeptAll(String accessToken) {
        // 获取部门列表
        JSONArray deptSimples = getDeptSimples(accessToken);
        if (deptSimples.isEmpty()) {
            throw new BusinessException("同步企业微信部门信息失败");
        }
        String tenantId = AuthConfigUtil.tenantId(SysConfigTypeEnum.WX_ENTERPRISE);
        List<Dept> deptList = deptSimples.parallelStream().map(d -> {
            JSONObject deptSimple = (JSONObject) JSONObject.toJSON(d);
            JSONObject dept = getDept(accessToken, deptSimple.get(DEPT_ID));
            String parentId = dept.getIntValue(DEPT_PARENT_ID) == ROOT_PARENT_ID ? tenantId : dept.getString(DEPT_PARENT_ID);
            return new Dept().setId(dept.getString(DEPT_ID)).setName(dept.getString(DEPT_NAME)).setSort(dept.getIntValue(DEPT_ORDER)).setParentId(parentId).setSource(AuthSourceEnum.WECHAT_ENTERPRISE_WEB);
        }).collect(Collectors.toList());
        deptList.removeIf(d -> d.getParentId().equals("0"));
        // 企业微信的部门排序为倒序，需改为升序(重新设置排序值)
        deptList = deptList.stream().sorted(Comparator.comparing(Dept::getSort).reversed()).collect(Collectors.toList());
        for (int i = 0; i < deptList.size(); i++) {
            deptList.get(i).setSort(i);
        }
        return deptList;
    }

    /**
     * 获取部门id列表
     * @param accessToken
     */
    private JSONArray getDeptSimples(String accessToken) {
        String deptSimpleUrl = UrlBuilder.fromBaseUrl(DEPT_ID_LIST_URL).queryParam(WxEnterpriseBase.ACCESS_TOKEN, accessToken).build();
        JSONObject jsonObject = HttpRequestUtils.getJson(deptSimpleUrl, JSONObject.class, Boolean.FALSE, null);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new BusinessException("获取企业微信部门失败");
        }
        if (jsonObject.containsKey(WxEnterpriseBase.ERR_CODE) && jsonObject.getIntValue(WxEnterpriseBase.ERR_CODE) != 0) {
            log.error("获取企业微信部门失败：{}", jsonObject);
            throw new BusinessException("获取企业微信部门失败");
        }
        return jsonObject.getJSONArray(DEPARTMENT_ID);
    }

    /**
     * 获取部门详情
     * @param accessToken
     * @param id 部门id
     * @return
     */
    public JSONObject getDept(String accessToken, Object id) {
        String deptUrl = UrlBuilder.fromBaseUrl(DEPT_DETAIL_URL).queryParam(WxEnterpriseBase.ACCESS_TOKEN, accessToken).queryParam(DEPT_ID, id).build();
        JSONObject jsonObject = HttpRequestUtils.getJson(deptUrl, JSONObject.class, Boolean.FALSE, null);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new BusinessException("获取企业微信部门详情失败");
        }
        if (jsonObject.containsKey(WxEnterpriseBase.ERR_CODE) && jsonObject.getIntValue(WxEnterpriseBase.ERR_CODE) != 0) {
            log.error("获取企业微信部门详情失败：{}", jsonObject);
            throw new BusinessException("获取企业微信部门详情失败");
        }
        return jsonObject.getJSONObject(DEPARTMENT);
    }
}
