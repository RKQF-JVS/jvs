package cn.bctools.auth.login.auth.wx;

import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.LoginHandler;
import cn.bctools.auth.login.dto.WxAppDto;
import cn.bctools.auth.service.*;
import cn.bctools.common.enums.OtherLoginTypeEnum;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.oss.template.OssTemplate;
import cn.bctools.redis.utils.RedisUtils;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 注册登录
 *
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Component("wxapp")
public class WxAppLoginHandler implements LoginHandler<WxAppDto> {
    OssTemplate ossTemplate;
    UserService userService;
    RedisUtils redisUtils;
    UserTenantService userTenantService;
    SmsEmailComponent smsComponent;
    RoleService roleService;
    UserRoleService userRoleService;
    UserExtensionService userExtensionService;


    final static String wxAppKey = "wxapp:sessionKey:";

    @SneakyThrows
    @Override
    public User handle(String code, String appId, WxAppDto wxAppDto) {
        WxMaUserService wxMaUserService = SpringContextUtil.getBean(WxMaService.class).getUserService();
        WxMaJscode2SessionResult userInfo = wxMaUserService.getSessionInfo(wxAppDto.getCode());
        String sessionKey = userInfo.getSessionKey();

        UserExtension wxapp = new UserExtension().setType(OtherLoginTypeEnum.wxapp.name()).setOpenId(userInfo.getOpenid());
        UserExtension userExtension = userExtensionService.getOne(Wrappers.lambdaQuery(wxapp));
        if (ObjectUtil.isNotEmpty(userExtension)) {
            //设置为10秒内
            redisUtils.set(wxAppKey + userExtension.getUserId(), sessionKey, Long.valueOf(10));
            return userService.getById(userExtension.getUserId());
        }
        //注册一个新的
        String idStr = IdGenerator.getIdStr(36);

        User user = new User().setAccountName(IdGenerator.getIdStr(36));
        //设置为前端用户
        user.setUserType(UserTypeEnum.FRONT_USER);
        UserTenant userTenant = new UserTenant().setRealName(idStr);
        userService.saveUser(user, userTenant);
        userRoleService.grandDefaultSysRole(user.getId());
        wxapp.setUserId(user.getId());
        wxapp.setExtension(BeanUtil.beanToMap(userInfo));
        userExtensionService.save(wxapp);
        //设置为10秒内
        redisUtils.set(wxAppKey + user.getId(), sessionKey, Long.valueOf(10));
        return user;
    }

    @Override
    public void bind(User user, String code, String appId) {
        String decodedPassword = PasswordUtil.decodedPassword(code, appId);
        WxAppDto callback = JSONObject.parseObject(decodedPassword, WxAppDto.class);
        WxMaUserService wxMaUserService = SpringContextUtil.getBean(WxMaService.class).getUserService();
        String sessionKey = wxAppKey + user.getId();
        if (!redisUtils.exists(sessionKey)) {
            return;
        }
        //从redis获取用户key  通过key调用获取用户信息
        sessionKey = (String) redisUtils.get(sessionKey);
        WxMaUserInfo userInfo = wxMaUserService.getUserInfo(sessionKey, callback.getEncryptedData(), callback.getIvStr());
        user.setRealName(userInfo.getNickName());
        // 微信头像持久化存储
        String avatar = getDurableAvatar(userInfo.getNickName(), userInfo.getAvatarUrl());
        UserExtension userExtension = userExtensionService.getOne(Wrappers.lambdaQuery(new UserExtension().setType(OtherLoginTypeEnum.wxapp.name()).setOpenId(userInfo.getOpenId())));
        userExtension.setNickname(userInfo.getNickName());
        //设置扩展字段
        userExtension.setExtension(JSONObject.parseObject(JSONObject.toJSONString(userInfo)));
        userExtensionService.updateById(userExtension);
        user.setRealName(userInfo.getNickName()).setHeadImg(avatar);
        userService.updateById(user);
    }


}
