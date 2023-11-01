package cn.bctools.auth.login;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.UserTypeEnum;
import cn.bctools.auth.login.dto.WxOfficialAccountsDto;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

/**
 * [description]：公众号扫码关注登录
 *
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Component("WECHAT_MP")
public class WxOfficialAccountsLoginHandler implements LoginHandler<WxOfficialAccountsDto> {
    private final static String OPEN_ID = "openId";
    private final static String WX_USER_NAME = "微信用户";
    private final UserService userService;
    private final RedisUtils redisUtils;
    private final UserExtensionService userExtensionService;
    private final UserRoleService userRoleService;

    @Override
    public User handle(String code, String appId, WxOfficialAccountsDto wxOfficialAccountsDto) {
        Object o = redisUtils.get(wxOfficialAccountsDto.getId());
        if (ObjectUtils.isNotEmpty(o)) {
            JSONObject jsonObject = JSONObject.parseObject((String) o);
            String openId = jsonObject.getString(OPEN_ID);
            User user;
            //判断用户是否存在
            UserExtension userExtension = userExtensionService.getOne(new LambdaQueryWrapper<UserExtension>().eq(UserExtension::getOpenId, openId));
            if (ObjectUtil.isNull(userExtension)) {
                // 注册
                user = new User()
                        .setRealName(WX_USER_NAME)
                        .setAccountName(IdGenerator.getIdStr(36))
                        .setCancelFlag(false);
                UserTenant userTenant = new UserTenant()
                        .setRealName(WX_USER_NAME)
                        .setCancelFlag(false);
                userService.saveUser(user, userTenant);
                userExtension = new UserExtension()
                        .setExtension(jsonObject.getInnerMap())
                        .setOpenId(openId)
                        .setNickname(WX_USER_NAME)
                        .setType(AuthDefaultSource.WECHAT_MP.name())
                        .setUserId(user.getId());
                //设置为前端用户
                user.setUserType(UserTypeEnum.FRONT_USER);
                userExtensionService.save(userExtension);
                // 默认为游客角色
                userRoleService.grandDefaultSysRole(user.getId());
            } else {
                user = userService.getById(userExtension.getUserId());
            }
            redisUtils.del(wxOfficialAccountsDto.getId());
            return user;
        }
        throw new BusinessException("用户不存在!");
    }

    @Override
    public void bind(User user, String code, String appId) {

    }
}
