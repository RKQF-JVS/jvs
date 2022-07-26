package cn.bctools.auth.component;

import cn.bctools.auth.entity.LoginLog;
import cn.bctools.auth.service.LoginLogService;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

/**
 * <登录日志>
 *
 * @author auto
 **/
@Slf4j
public class LoginLogComponent {
    public static void successLog(Authentication authentication) {
        UserDto userDto = ((UserInfoDto) authentication.getPrincipal()).getUserDto();
        //登录成功日志记录
        LoginLog copy = BeanCopyUtil.copy(userDto, LoginLog.class);
        copy.setId(null);
        copy.setOperateTime(LocalDateTime.now());
        copy.setStatus(true).setUserId(userDto.getId());
        //直接保存用户登录信息
        SpringContextUtil.getBean(LoginLogService.class).save(copy);
    }


}
