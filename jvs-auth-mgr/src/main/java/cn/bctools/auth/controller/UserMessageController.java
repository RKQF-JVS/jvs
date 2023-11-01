package cn.bctools.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.UserMessageLog;
import cn.bctools.auth.service.UserMessageLogService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author guojing
 */
@Slf4j
@RestController
@RequestMapping("userlog")
@AllArgsConstructor
@Api(tags = "用户消息")
public class UserMessageController {

    UserMessageLogService userLogService;

    @Log
    @ApiOperation("消息搜索")
    @GetMapping("/page")
    public R<Page<UserMessageLog>> page(Page page, @RequestParam(required = false) String search) {
        QueryWrapper<UserMessageLog> queryWrapper = new QueryWrapper<>();
        //获取登录用户
        String id = UserCurrentUtils.getCurrentUser().getId();
        queryWrapper.lambda().eq(UserMessageLog::getUserId, id).eq(UserMessageLog::getHide, false).orderByDesc(UserMessageLog::getCreateTime);
        if (ObjectUtil.isNotEmpty(search)) {
            queryWrapper.and(e -> e.lambda().like(UserMessageLog::getTitle, search).or().like(UserMessageLog::getContent, search));
        }
        userLogService.page(page, queryWrapper);
        return R.ok(page);
    }

    @Log
    @ApiOperation("隐藏消息")
    @DeleteMapping("/hide/{id}")
    public R hide(@PathVariable String id) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        userLogService.updateById(new UserMessageLog().setHide(true).setId(id).setUserId(userId));
        return R.ok();
    }

    @Log
    @ApiOperation("标记已读")
    @PutMapping("/read/{id}")
    public R read(@PathVariable String id) {
        //获取当前用户
        String userId = UserCurrentUtils.getCurrentUser().getId();
        userLogService.updateById(new UserMessageLog().setReadStatus(true).setId(id).setUserId(userId));
        return R.ok();
    }

    @Log
    @ApiOperation("未读总数")
    @GetMapping("/unread")
    public R unread() {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        //修改查询状态为未读状态
        int count = userLogService.count(Wrappers.query(new UserMessageLog().setReadStatus(false).setHide(false).setUserId(userId)));
        return R.ok(count);
    }

}
