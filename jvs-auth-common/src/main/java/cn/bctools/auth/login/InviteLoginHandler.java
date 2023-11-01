package cn.bctools.auth.login;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.login.dto.InviteDto;
import cn.bctools.auth.service.RoleService;
import cn.bctools.auth.service.UserRoleService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.redis.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 邀请码登录，由后台管理人员自己邀请并注册用户
 *
 * @author Administrator
 */
@Slf4j
@AllArgsConstructor
@Component("invite")
public class InviteLoginHandler implements LoginHandler<InviteDto> {

    RedisUtils redisUtils;
    UserService userService;
    RoleService roleService;
    UserRoleService userRoleService;
    UserTenantService userTenantService;

    @Override
    public User handle(String code, String appId, InviteDto inviteDto) {
        TenantContextHolder.setTenantId(inviteDto.getId());
        //如果没有自己注册
        User one = userService.getOne(Wrappers.query(new User().setPhone(inviteDto.getPhone())));
        if (ObjectUtil.isNotEmpty(one)) {
            //判断用户是否在租户中存在
            UserTenant tenantUser = userTenantService.getOne(Wrappers.query(new UserTenant().setUserId(one.getId()).setTenantId(inviteDto.getId())));
            if (ObjectUtil.isNotEmpty(tenantUser)) {
                if (tenantUser.getCancelFlag()) {
                    throw new BusinessException("您已被公司禁用");
                } else {
                    //已加入组织,自己去登录即可
                    return one;
                }
            } else {
                UserTenant userTenant = new UserTenant().setRealName(inviteDto.getRealName()).setUserId(one.getId());
                userTenantService.save(userTenant);
                //创建租户信息，返回用户信息
                return one;
            }
        } else {
            User user = new User().setPhone(inviteDto.getPhone()).setRealName(inviteDto.getRealName());
            user.setCancelFlag(false);
            UserTenant userTenant = new UserTenant().setRealName(inviteDto.getRealName());
            UserDto o = (UserDto) redisUtils.get(inviteDto.getInvite());
            //将邀请用户ID拿出来
            user.setInvite(o.getId());
            userService.saveUser(user, userTenant);
            userRoleService.grandDefaultSysRole(user.getId());
            log.info("邀请新用户加入:{}  邀请 {} 加入团队", o.getRealName(), user.getRealName());
            return user;
        }
    }


    @Override
    public void bind(User user, String code, String appId) {

    }

}
