package cn.bctools.auth.login;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.login.dto.RegisterDto;
import cn.bctools.auth.service.RoleService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * 注册登录
 *
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Component("register")
public class RegisterLoginHandler implements LoginHandler<RegisterDto> {

    UserService userService;
    UserTenantService userTenantService;
    SmsEmailComponent smsComponent;
    RoleService roleService;
    UserRoleService userRoleService;

    /**
     * 字母正则
     */
    static final Pattern WORD_PATTERN = Pattern.compile("^[A-Za-z]+$");

    @Override
    public User handle(String code, String appId, RegisterDto registerDto) {
        //校验是否成功
        smsComponent.check(registerDto.getPhone(), registerDto.getCode(), () -> new BusinessException("验证失败"));
        User user = new User().setPhone(registerDto.getPhone()).setRealName(registerDto.getRealName());

        user.setAccountName(IdGenerator.getIdStr(36));
        //看库里面是否存在
        if (ObjectUtil.isEmpty(userService.getOne(Wrappers.lambdaQuery(new User().setRealName(registerDto.getRealName()))))) {
            if (WORD_PATTERN.matcher(registerDto.getRealName()).matches()) {
                user.setAccountName(registerDto.getRealName());
            }
        }

        user.setCancelFlag(false);
        UserTenant userTenant = new UserTenant().setRealName(registerDto.getRealName());
        userService.saveUser(user, userTenant);
        userRoleService.grandDefaultSysRole(user.getId());
        return user;
    }

    @Override
    public void bind(User user, String code, String appId) {

    }

}
