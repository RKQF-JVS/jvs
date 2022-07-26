package cn.bctools.auth.login;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.login.dto.WxAppDto;
import cn.bctools.auth.service.*;
import cn.bctools.common.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册登录
 *
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Component("wxapp")
public class WxAppLoginHandler implements LoginHandler<WxAppDto> {

    UserService userService;
    UserTenantService userTenantService;
    SmsEmailComponent smsComponent;
    RoleService roleService;
    UserRoleService userRoleService;
    UserExtensionService userExtensionService;

    @Override
    public User handle(String code, String appId, WxAppDto wxAppDto) {
        UserExtension wxapp = new UserExtension().setType("wxapp").setOpenId(wxAppDto.getOpenId());
        wxapp.setNickname(wxAppDto.getNickname());
        UserExtension userExtension = userExtensionService.getOne(Wrappers.lambdaQuery(wxapp));
        if (ObjectUtil.isNotEmpty(userExtension)) {
            return userService.getById(userExtension.getUserId());
        }
        //注册一个新的
        String idStr = IdGenerator.getIdStr(36);
        User user = new User().setAccountName(IdGenerator.getIdStr(36)).setRealName(idStr);
        user.setCancelFlag(false);
        UserTenant userTenant = new UserTenant().setRealName(idStr);
        userService.saveUser(user, userTenant);
        userRoleService.grandDefaultSysRole(user.getId());
        wxapp.setUserId(user.getId());
        userExtensionService.save(wxapp);
        return user;
    }

    @Override
    public void bind(User user, String code, String appId) {

    }

}
