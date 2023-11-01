package cn.bctools.auth.controller;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserInvite;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.mapper.UserInviteMapper;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.vo.InviteVo;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.redis.utils.RedisUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Api(value = "邀请用户", tags = "邀请用户待审核")
@RestController
@RequestMapping("/invite")
public class InviteController {

    RedisUtils redisUtils;
    UserTenantService userTenantService;
    UserInviteMapper userInviteMapper;
    UserService userService;

    @Log
    @ApiOperation(value = "用户填写邀请码", notes = "用户访问邀请页面，填写邀请码")
    @PutMapping("/{code}")
    public R code(@PathVariable String code) {
        UserDto currentUser = UserCurrentUtils.getCurrentUser();
        //判断是否需要审核
        Object status = redisUtils.get(SysConstant.redisKey("invite", code));
        if (ObjectNull.isNotNull(status)) {
            InviteVo st = (InviteVo) status;
            //将当前用户插入到对应的租户中去
            TenantContextHolder.setTenantId(String.valueOf(st.getTenantId()));
            //判断此用户是否在租户里面
            UserTenant one = userTenantService.getOne(Wrappers.query(new UserTenant().setUserId(currentUser.getId())));
            if (ObjectNull.isNotNull(one)) {
                return R.ok().setMsg("已加入组织");
            }
            if (st.getStatus()) {
                UserInvite entity = new UserInvite().setUserId(currentUser.getId());
                UserInvite userInvite = userInviteMapper.selectOne(Wrappers.query(entity));
                if (ObjectNull.isNull(userInvite)) {
                    userInviteMapper.insert(entity);
                }
                return R.ok().setMsg("待管理员审核");
            } else {
                int countUser = userTenantService.count(Wrappers.query(new UserTenant().setUserId(currentUser.getId())));
                if (countUser == 0) {
                    //查看是否存在
                    userTenantService.saveOrUpdate(new UserTenant().setUserId(currentUser.getId()).setPhone(currentUser.getPhone()).setRealName(currentUser.getRealName()));
                }
            }
            return R.ok().setMsg("已加入组织");
        }
        return R.failed("邀请码已过期");
    }

    @Log
    @ApiOperation(value = "审核通过|拒绝", notes = "通过或拒绝 true|false")
    @PutMapping("/status/{status}/{userId}")
    public R status(@PathVariable Boolean status, @PathVariable String userId) {
        if (status) {
            User user = userService.getById(userId);
            int countUser = userTenantService.count(Wrappers.query(new UserTenant().setUserId(user.getId())));
            if (countUser == 0) {
                //查看是否存在
                userTenantService.saveOrUpdate(new UserTenant().setUserId(user.getId()).setPhone(user.getPhone()).setRealName(user.getRealName()));
            }
        }
        userInviteMapper.delete(Wrappers.query(new UserInvite().setUserId(userId)));
        return R.ok("操作成功");
    }

    @Log(back = false)
    @ApiOperation(value = "分页")
    @GetMapping("/page")
    public R<Page<User>> page(Page<UserInvite> page) {
        userInviteMapper.selectPage(page, Wrappers.query());
        Page<User> userPage = new Page<User>().setSize(page.getSize()).setCurrent(page.getCurrent());
        List<String> collect = page.getRecords().stream().map(e -> e.getUserId()).collect(Collectors.toList());
        if (ObjectNull.isNull(collect)) {
            return R.ok(userPage);
        }
        List<User> users = userService.listByIds(collect);
        userPage.setRecords(users);
        return R.ok(userPage);
    }

}
