package cn.bctools.auth.login.auth.ding;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.dto.DingtalkDto;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.PasswordUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetbyunionidRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetbyunionidResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Author: ZhuXiaoKang
 * @Description: 钉钉扫码登录
 */
@Slf4j
@Component("DINGTALK_SCAN")
public class DdScanLoginHandler extends DdBase implements LoginHandler<DingtalkDto> {
    private static final String GET_USER_INFO_BY_CODE_URL = "https://oapi.dingtalk.com/sns/getuserinfo_bycode";
    private static final String GET_USER_ID_BY_UNIONID_URL = "https://oapi.dingtalk.com/topapi/user/getbyunionid";
    private static final String GET_USER_INFO_BY_ID_URL = " https://oapi.dingtalk.com/topapi/v2/user/get";

    static {
        configType = SysConfigTypeEnum.DING_H5;
    }
    @Override
    public User handle(String code, String appId, DingtalkDto dto) {
        if (StringUtils.isBlank(AuthConfigUtil.appId(configType)) || StringUtils.isBlank(AuthConfigUtil.agentId(configType)) ||
                StringUtils.isBlank(AuthConfigUtil.secret(configType)) || StringUtils.isBlank(AuthConfigUtil.redirectUri(configType))) {
            throw new BusinessException("请完善授权配置");
        }
        OapiSnsGetuserinfoBycodeResponse.UserInfo userInfo = getUserInfoByCode(dto.getCode());
        String accessToken = getAccessToken();
        String userId = getUserIdByUnionid(userInfo.getUnionid(), accessToken);
        OapiV2UserGetResponse.UserGetResponse userResp = getUserInfoByUserId(userId, accessToken);
        if (userResp == null) {
            throw new BusinessException("获取钉钉用户失败");
        }
        return getUserInfo(LOGIN_TYPE, userResp);
    }

    @SneakyThrows
    private OapiSnsGetuserinfoBycodeResponse.UserInfo getUserInfoByCode(String code) {
        DingTalkClient client = new DefaultDingTalkClient(GET_USER_INFO_BY_CODE_URL);
        OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
        req.setTmpAuthCode(code);
        OapiSnsGetuserinfoBycodeResponse rsp = client.execute(req, AuthConfigUtil.appId(configType), AuthConfigUtil.secret(configType));
        if (DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("钉钉扫码登录失败。根据临时授权码获取用户信息失败：{}", rsp.getBody());
            throw new BusinessException("获取钉钉用户失败");
        }
        return rsp.getUserInfo();
    }

    @SneakyThrows
    private String getUserIdByUnionid(String unionid, String accessToken) {
        DingTalkClient client = new DefaultDingTalkClient(GET_USER_ID_BY_UNIONID_URL);
        OapiUserGetbyunionidRequest req = new OapiUserGetbyunionidRequest();
        req.setUnionid(unionid);
        OapiUserGetbyunionidResponse rsp = client.execute(req, accessToken);
        if (DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("钉钉扫码登录失败。根据unionid获取用户信息失败：{}", rsp.getBody());
            throw new BusinessException("获取钉钉用户失败");
        }
        return rsp.getResult().getUserid();
    }

    @SneakyThrows
    private OapiV2UserGetResponse.UserGetResponse getUserInfoByUserId(String userId, String accessToken) {
        DingTalkClient client = new DefaultDingTalkClient(GET_USER_INFO_BY_ID_URL);
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        req.setUserid(userId);
        req.setLanguage("zh_CN");
        OapiV2UserGetResponse rsp = client.execute(req, accessToken);
        if (DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("钉钉扫码登录失败。根据用户id获取用户信息失败：{}", rsp.getBody());
            throw new BusinessException("获取钉钉用户失败");
        }
        return rsp.getResult();
    }

    @Override
    public void bind(User user, String code, String appId) {
        String decodedPassword = PasswordUtil.decodedPassword(code, appId);
        DingtalkDto dto = JSONObject.parseObject(decodedPassword, DingtalkDto.class);
        OapiSnsGetuserinfoBycodeResponse.UserInfo authUser = getUserInfoByCode(dto.getCode());
        String accessToken = getAccessToken();
        String userId = getUserIdByUnionid(authUser.getUnionid(), accessToken);
        OapiV2UserGetResponse.UserGetResponse userResp = getUserInfoByUserId(userId, accessToken);
        if (userResp == null) {
            throw new BusinessException("获取钉钉用户失败");
        }
        log.info("[bind] 获取钉钉用户信息: {}", JSONUtil.toJsonStr(userResp));
        String nickname = userResp.getName();
        String openId = userResp.getUnionid();
        UserExtension extension = userExtensionService.getOne(Wrappers.query(new UserExtension().setType(LOGIN_TYPE).setOpenId(openId)));
        // 判断是否重复绑定
        if (ObjectUtil.isNotEmpty(extension)) {
            throw new BusinessException("钉钉已绑定其它帐号");
        }
        // 绑定用户关键信息
        extension = new UserExtension()
                .setOpenId(openId)
                .setNickname(nickname)
                .setUserId(user.getId())
                .setType(LOGIN_TYPE)
                .setExtension(JSONObject.parseObject(JSONObject.toJSONString(userResp)));
        userService.updateById(user);
        userExtensionService.save(extension);
    }

    @Override
    public Object getLoginUserInfo(Object userObj) {
        return userObj;
    }
}
