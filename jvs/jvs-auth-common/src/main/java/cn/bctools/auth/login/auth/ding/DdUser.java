package cn.bctools.auth.login.auth.ding;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.util.SyncOrgUtils;
import cn.bctools.common.enums.OtherLoginTypeEnum;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.ObjectNull;
import cn.hutool.core.bean.BeanUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description: 钉钉用户
 */
@Slf4j
@Component
@AllArgsConstructor
public class DdUser {

    private static final String DEPT_USER_LIST_URL = "https://oapi.dingtalk.com/topapi/v2/user/list";
    private final DdDept ddDept;

    /**
     * 获取部门所有用户
     * @param accessToken
     * @param depts
     * @return
     */
    public SyncUserDto getDeptUserAll(String accessToken, List<Dept> depts) {
        SyncUserDto syncUserDto = new SyncUserDto();
        if (CollectionUtils.isNotEmpty(depts)) {
            depts.parallelStream().forEach(dept -> {
                List<OapiV2UserListResponse.ListUserResponse> deptUsers = Collections.synchronizedList(new ArrayList<>());
                getPageDeptUsers(accessToken, Long.parseLong(dept.getId()), 0L, deptUsers);
                convertUser(syncUserDto, dept, deptUsers);
            });
        }
        // 获取钉钉部门时，排除了根部门。获取用户信息时需要获取根部门下的用户
        OapiV2DepartmentGetResponse.DeptGetResponse rootDept = ddDept.getDept(accessToken, DdDept.ROOT_PARENT_ID);
        List<OapiV2UserListResponse.ListUserResponse> deptUsers = new ArrayList<>();
        getPageDeptUsers(accessToken, rootDept.getDeptId(), 0L, deptUsers);
        convertUser(syncUserDto, null, deptUsers);
        return syncUserDto;
    }

    /**
     * 分页获取部门用户
     * @param accessToken
     * @param deptId
     * @param cursor
     * @param userResponses
     */
    private void getPageDeptUsers(String accessToken, Long deptId, Long cursor, List<OapiV2UserListResponse.ListUserResponse> userResponses) {
        OapiV2UserListResponse.PageResult result = getDeptUser(accessToken, deptId, cursor);
        if (CollectionUtils.isEmpty(result.getList())) {
            return;
        }
        userResponses.addAll(result.getList());
        if (result.getHasMore()) {
            getPageDeptUsers(accessToken, deptId, result.getNextCursor(), userResponses);
        }
    }

    /**
     * 获取部门详情
     * @param accessToken
     * @param deptId 部门id
     */
    @SneakyThrows
    private OapiV2UserListResponse.PageResult getDeptUser(String accessToken, Long deptId, Long cursor) {
        DingTalkClient client = new DefaultDingTalkClient(DEPT_USER_LIST_URL);
        OapiV2UserListRequest req = new OapiV2UserListRequest();
        req.setDeptId(deptId);
        req.setCursor(cursor);
        req.setSize(100L);
        req.setLanguage("zh_CN");
        OapiV2UserListResponse rsp = client.execute(req, accessToken);
        if (DdBase.DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("获取钉钉部门用户失败：{}", rsp.getBody());
            throw new BusinessException("获取钉钉部门用户失败");
        }
        return rsp.getResult();
    }

    /**
     * 得到要同步的用户信息
     * @param syncUserDto 转换后的用户信息
     * @param dept 三方平台部门
     * @param deptUsers 三方平台部门用户集合
     */
    private void convertUser(SyncUserDto syncUserDto, Dept dept, List<OapiV2UserListResponse.ListUserResponse> deptUsers) {
        if (deptUsers.isEmpty()) {
            return;
        }
        String tenantId = AuthConfigUtil.tenantId(SysConfigTypeEnum.DING_H5);
        deptUsers.parallelStream().forEach(deptUser -> {
            String openId = deptUser.getUnionid();
            if (StringUtils.isNotBlank(openId)) {
                String nickname = deptUser.getName();
                User user = new User()
                        .setId(openId)
                        .setRealName(nickname)
                        .setAccountName(IdGenerator.getIdStr(36))
                        .setCancelFlag(false)
                        .setUserType(UserTypeEnum.OTHER_USER);
                UserTenant userTenant = new UserTenant()
                        .setUserId(user.getId())
                        .setRealName(nickname)
                        .setCancelFlag(false);
                if (ObjectNull.isNotNull(dept)) {
                    userTenant.setDeptId(SyncOrgUtils.buildDeptId(tenantId, dept.getId())).setDeptName(dept.getName());
                }
                UserExtension userExtension = new UserExtension()
                        .setExtension(BeanUtil.beanToMap(deptUser))
                        .setOpenId(openId)
                        .setNickname(nickname)
                        .setType(OtherLoginTypeEnum.Ding.name())
                        .setUserId(user.getId());

                syncUserDto.getUsers().add(user);
                syncUserDto.getUserTenants().add(userTenant);
                syncUserDto.getUserExtensions().add(userExtension);
            }
        });

    }
}
