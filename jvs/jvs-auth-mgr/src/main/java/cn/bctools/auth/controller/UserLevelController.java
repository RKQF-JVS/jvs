package cn.bctools.auth.controller;

import cn.bctools.auth.entity.UserLevel;
import cn.bctools.auth.service.UserLevelService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/level")
public class UserLevelController {
    UserLevelService userLevelService;
    UserTenantService userTenantService;

    @Log(back = false)
    @ApiOperation(value = "所有用户等级单选", notes = "每一个用户等级都可以设置自己的首页地址")
    @GetMapping("/dict")
    public R levelSelect() {
        List<UserLevel> list = userLevelService.list();
        return R.ok(list);
    }

    @Log(back = false)
    @ApiOperation(value = "所有用户等级", notes = "每一个用户等级都可以设置自己的首页地址")
    @GetMapping("/all")
    public R<List<UserLevel>> level() {
        List<UserLevel> list = userLevelService.list();
        return R.ok(list);
    }

    @ApiOperation(value = "新增用户等级", notes = "每一个用户等级都可以设置自己的首页地址")
    @PostMapping
    public R saveLevel(@RequestBody UserLevel userLevel) {
        //判断是否重复
        int count = userLevelService.count(Wrappers.query(new UserLevel().setName(userLevel.getName())));
        if (count > 0) {
            return R.failed("用户等级不允许重复");
        }
        userLevelService.save(userLevel);
        return R.ok();
    }

    @ApiOperation(value = "修改用户等级", notes = "每一个用户等级都可以设置自己的首页地址")
    @PutMapping
    public R editLevel(@RequestBody UserLevel userLevel) {
        int count = userLevelService.count(Wrappers.query(new UserLevel().setName(userLevel.getName())));
        if (count > 1) {
            return R.failed("用户等级不允许重复");
        }
        userLevelService.updateById(userLevel);
        return R.ok();
    }

    @ApiOperation(value = "删除用户等级", notes = "每一个用户等级都可以设置自己的首页地址")
    @DeleteMapping("/{id}")
    public R deleteLevel(@PathVariable String id) {
        userLevelService.removeById(id);
        return R.ok();
    }

}
