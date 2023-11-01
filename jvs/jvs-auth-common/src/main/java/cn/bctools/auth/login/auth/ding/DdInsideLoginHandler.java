package cn.bctools.auth.login.auth.ding;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.dto.DingtalkDto;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.auth.login.util.AuthConfigUtil;
import cn.bctools.common.exception.BusinessException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserGetuserinfoRequest;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description: 钉钉内部应用免登
 */
@Slf4j
@AllArgsConstructor
@Component("DINGTALK_INSIDE")
public class DdInsideLoginHandler extends DdBase implements LoginHandler<DingtalkDto> {
    private static final String GET_USER_INFO_URL = "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo";
    DdDept ddDept;
    DdUser ddUser;

    static {
        configType = SysConfigTypeEnum.DING_H5;
    }

    @Override
    public User handle(String code, String appId, DingtalkDto dto) {
        if (StringUtils.isBlank(AuthConfigUtil.appId(configType)) || StringUtils.isBlank(AuthConfigUtil.agentId(configType)) ||
                StringUtils.isBlank(AuthConfigUtil.secret(configType))) {
            throw new BusinessException("请完善授权配置");
        }
        if (StringUtils.isBlank(dto.getAgentId())) {
            throw new BusinessException("未配置应用agentId");
        }
        if (Boolean.FALSE.equals(AuthConfigUtil.agentId(configType).equals(dto.getAgentId()))) {
            throw new BusinessException("agentId配置错误");
        }
        String accessToken = getAccessToken();
        OapiV2UserGetuserinfoResponse.UserGetByCodeResponse userResp = getDingUserInfo(accessToken, dto.getCode());
        if (userResp == null) {
            throw new BusinessException("登录失败");
        }
        return getUserInfo(LOGIN_TYPE, userResp);
    }

    @SneakyThrows
    private OapiV2UserGetuserinfoResponse.UserGetByCodeResponse getDingUserInfo(String accessToken, String code) {
        DingTalkClient client = new DefaultDingTalkClient(GET_USER_INFO_URL);
        OapiV2UserGetuserinfoRequest req = new OapiV2UserGetuserinfoRequest ();
        req.setCode(code);
        OapiV2UserGetuserinfoResponse rsp = client.execute(req, accessToken);
        if (DINGTALK_SUCCESS_CODE.intValue() != rsp.getErrcode().intValue()) {
            log.error("钉钉免登失败。获取钉钉用户信息失败：{}", rsp.getBody());
            throw new BusinessException("登录失败");
        }
        return rsp.getResult();
    }

    @Override
    public void bind(User user, String code, String appId) {

    }

    @Override
    public Object getLoginUserInfo(Object userObj) {
        return userObj;
    }

    @Override
    public List<Dept> getDeptAll() {
        String accessToken = getAccessToken();
        return ddDept.getDeptAll(accessToken);
    }

    @Override
    public SyncUserDto getDeptUserAll(List<Dept> depts) {
        String accessToken = getAccessToken();
        return ddUser.getDeptUserAll(accessToken, depts);
    }
}
