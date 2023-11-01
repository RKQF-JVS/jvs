package cn.bctools.auth.login.auth.ding;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.enums.OtherLoginTypeEnum;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: ZhuXiaoKang
 * @Description: 钉钉登录通用处理
 */
@Slf4j
@Component
public abstract class DdBase {
    public static final String LOGIN_TYPE = OtherLoginTypeEnum.Ding.name();
    public static SysConfigTypeEnum configType;
    public static final Long DINGTALK_SUCCESS_CODE = 0L;
    // 钉钉access_token缓存
    private static final String ACCESS_TOKEN_KEY = SysConstant.JVS_AUTH + "ding:access_token:";
    // 员工在当前开发者企业账号范围内的唯一标识
    public static final String UNIONID = "unionid";
    // 姓名
    private static final String NAME = "name";
    private static final String GET_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";

    @Resource
    UserService userService;
    @Resource
    UserRoleService userRoleService;
    @Resource
    UserExtensionService userExtensionService;
    @Resource
    RedisUtils redisUtils;

    /**
     * 获取AccessToken
     * @return
     */
    @SneakyThrows
    public String getAccessToken() {
        // ACCESS_TOKEN缓存key：前缀 + 租户id + agentId
        String accessTokenCacheKey = ACCESS_TOKEN_KEY + AuthConfigUtil.tenantId(configType) + ":" + AuthConfigUtil.agentId(configType);
        Object accessToken = redisUtils.get(accessTokenCacheKey);
        if (ObjectNull.isNotNull(accessToken)) {
            return (String)accessToken;
        }
        // 缓存中无accessToken，发起请求获取accessToken
        DingTalkClient client = new DefaultDingTalkClient(GET_TOKEN_URL);
        OapiGettokenRequest req = new OapiGettokenRequest();
        req.setAppkey(AuthConfigUtil.appId(configType));
        req.setAppsecret(AuthConfigUtil.secret(configType));
        req.setHttpMethod(HttpMethod.GET.name());
        OapiGettokenResponse rsp = client.execute(req);
        if (DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("钉钉免登失败。获取access_token失败：{}", rsp.getBody());
            throw new BusinessException("钉钉获取AccessToken失败");
        }
        // 缓存accessToken
        Long expire = rsp.getExpiresIn() - 100;
        redisUtils.set(accessTokenCacheKey, rsp.getAccessToken(), expire);
        String token = rsp.getAccessToken();
        if (StringUtils.isBlank(token)) {
            log.error("登录失败，获取accessToken失败");
            throw new BusinessException("钉钉获取AccessToken失败");
        }
        return token;
    }

    /**
     * 获取用户信息
     * @param loginType
     * @param userResp 三方登录返回的用户信息
     * @return 用户信息
     */
    public User getUserInfo(String loginType, Object userResp) {
        JSONObject userObj = JSON.parseObject(JSON.toJSONString(userResp));
        // 获取用户信息
        UserExtension extension = userExtensionService.getOne(Wrappers.<UserExtension>lambdaQuery()
                .eq(UserExtension::getType, loginType)
                .eq(UserExtension::getOpenId, userObj.getString(UNIONID)));
        if (ObjectUtil.isNotEmpty(extension)) {
            return userService.getById(extension.getUserId());
        }
        // 注册用户
        userObj = JSON.parseObject(JSON.toJSONString(getLoginUserInfo(userResp)));
        return registerUser(loginType, userObj);
    }

    /**
     * 获取三方登录用户信息
     * @param obj
     * @return
     */
    public abstract Object getLoginUserInfo(Object obj);

    /**
     * 注册用户
     * @param loginType
     * @param userObj
     */
    private User registerUser(String loginType, JSONObject userObj) {
        String realName = userObj.getString(NAME);
        // 注册
        User user = new User().setRealName(realName).setAccountName(IdGenerator.getIdStr(36));
        UserTenant userTenant = new UserTenant().setRealName(realName).setTenantId(AuthConfigUtil.tenantId(configType));
        userService.saveUser(user, userTenant);
        UserExtension userExtension = new UserExtension()
                .setExtension(JSON.parseObject(JSON.toJSONString(userObj)))
                .setOpenId(userObj.getString(UNIONID))
                .setNickname(realName)
                .setType(loginType)
                .setUserId(user.getId());
        //设置为前端用户
        user.setUserType(UserTypeEnum.OTHER_USER);
        userExtensionService.save(userExtension);
        // 默认为游客角色
        userRoleService.grandDefaultSysRole(user.getId());
        return user;
    }
}
