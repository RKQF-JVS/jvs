package cn.bctools.auth.login.auth.wx.enterprise;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.util.SyncOrgUtils;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.web.utils.HttpRequestUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.UrlBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description: 企业微信用户
 */
@Slf4j
@Component
@AllArgsConstructor
public class WxEnterpriseUser extends WxEnterpriseBase{

    /**
     * 全局唯一。对于同一个服务商，不同应用获取到企业内同一个成员的open_userid是相同的，最多64个字节
     */
    private static final String USER_LIST = "userlist";
    private static final String USER_NAME = "name";
    private static final String DEPARTMENT_ID = "department_id";
    private static final String DEPT_USER_SIMPLE_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist";

    private final WxEnterpriseDept wxEnterpriseDept;

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
                JSONArray deptUsers = getDeptUsers(accessToken, dept.getId());
                convertUser(syncUserDto, accessToken, dept, deptUsers);
            });
        }
        // 获取企业微信部门时，排除了根部门。获取用户信息时需要获取根部门下的用户
        JSONObject rootDept = wxEnterpriseDept.getDept(accessToken, WxEnterpriseDept.ROOT_PARENT_ID);
        JSONArray deptUsers = getDeptUsers(accessToken, rootDept.getString(WxEnterpriseDept.DEPT_ID));
        convertUser(syncUserDto, accessToken,null, deptUsers);
        return syncUserDto;
    }

    /**
     * 获取部门成员列表
     * @param accessToken
     * @param deptId
     */
    private JSONArray getDeptUsers(String accessToken, String deptId) {
        String deptUserUrl = UrlBuilder.fromBaseUrl(DEPT_USER_SIMPLE_LIST_URL)
                .queryParam(WxEnterpriseBase.ACCESS_TOKEN, accessToken)
                .queryParam(DEPARTMENT_ID, deptId)
                .build();
        JSONObject jsonObject = HttpRequestUtils.getJson(deptUserUrl, JSONObject.class, Boolean.FALSE, null);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new BusinessException("获取企业微信部门用户失败");
        }
        if (jsonObject.containsKey(WxEnterpriseBase.ERR_CODE) && jsonObject.getIntValue(WxEnterpriseBase.ERR_CODE) != 0) {
            log.error("获取企业微信部门用户失败: {}", jsonObject);
            throw new BusinessException("获取企业微信部门用户失败");
        }
        return jsonObject.getJSONArray(USER_LIST);
    }

    /**
     * 得到要同步的用户信息
     * @param syncUserDto 转换后的用户信息
     * @param accessToken 三方平台授权token
     * @param dept 三方平台部门
     * @param deptUsers 三方平台部门用户集合
     */
    private void convertUser(SyncUserDto syncUserDto, String accessToken, Dept dept, JSONArray deptUsers) {
        if (deptUsers.isEmpty()) {
            return;
        }
        String tenantId = AuthConfigUtil.tenantId(SysConfigTypeEnum.WX_ENTERPRISE);
        deptUsers.parallelStream().forEach(u -> {
            JSONObject deptUser = (JSONObject) JSONObject.toJSON(u);
            String deptUserId = deptUser.getString(USER_ID);
            String openId = getOpenId(accessToken, deptUserId);
            if (StringUtils.isNotBlank(openId)) {
                String nickname = deptUser.getString(USER_NAME);
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
                AuthUser authUser = AuthUser.builder()
                        .rawUserInfo(deptUser)
                        .username(deptUser.getString("name"))
                        .nickname(deptUser.getString("alias"))
                        .avatar(deptUser.getString("avatar"))
                        .location(deptUser.getString("address"))
                        .email(deptUser.getString("email"))
                        .uuid(deptUserId)
                        .gender(AuthUserGender.getWechatRealGender(deptUser.getString("gender")))
                        .build();
                UserExtension userExtension = new UserExtension()
                        .setExtension(BeanUtil.beanToMap(authUser))
                        .setOpenId(openId)
                        .setNickname(nickname)
                        .setType(LOGIN_TYPE)
                        .setUserId(user.getId());

                syncUserDto.getUsers().add(user);
                syncUserDto.getUserTenants().add(userTenant);
                syncUserDto.getUserExtensions().add(userExtension);
            }
        });

    }

    @Override
    String getDurableAvatar(String nickname, String fileUrl) {
        return null;
    }
}
