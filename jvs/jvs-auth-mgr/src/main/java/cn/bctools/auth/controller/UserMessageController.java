package cn.bctools.auth.controller;

import cn.bctools.auth.entity.UserMessageLog;
import cn.bctools.auth.entity.enums.SendMessageTypeEnum;
import cn.bctools.auth.service.UserMessageLogService;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 
 */
@Slf4j
@RestController
@RequestMapping("userlog")
@AllArgsConstructor
@Api(tags = "用户消息")
public class UserMessageController {

    UserMessageLogService userLogService;

    @Log
    @ApiOperation("消息类型")
    @GetMapping("/type")
    public R<List<String>> type() {
        return R.ok(Arrays.stream(SendMessageTypeEnum.values()).map(e -> e.name()).collect(Collectors.toList()));
    }

    @Log
    @ApiOperation("查询最近的10条消息")
    @GetMapping("/newest")
    public R<List<UserMessageLog>> newest() {
        LambdaQueryWrapper<UserMessageLog> queryWrapper = new LambdaQueryWrapper<UserMessageLog>();
        //获取登录用户
        String id = UserCurrentUtils.getCurrentUser().getId();
        queryWrapper.orderByDesc(UserMessageLog::getCreateTime);
        queryWrapper.eq(UserMessageLog::getUserId, id);
        List<UserMessageLog> list = userLogService.list(queryWrapper);
        return R.ok(list);
    }

    @Log
    @ApiOperation("消息搜索")
    @GetMapping("/page")
    public R<Page<UserMessageLog>> page(Page page, @RequestParam(required = false) Boolean readStatus, @RequestParam(required = false) String search, @RequestParam(required = false) SendMessageTypeEnum type) {
        LambdaQueryWrapper<UserMessageLog> queryWrapper = new LambdaQueryWrapper<UserMessageLog>();
        //获取登录用户
        String id = UserCurrentUtils.getCurrentUser().getId();
        queryWrapper.eq(ObjectNull.isNotNull(type), UserMessageLog::getSendMessageType, type).eq(UserMessageLog::getUserId, id).eq(UserMessageLog::getHide, false).orderByDesc(UserMessageLog::getCreateTime);
        if (ObjectUtil.isNotEmpty(search)) {
            queryWrapper.and(e -> e.like(UserMessageLog::getTitle, search).or().like(UserMessageLog::getContent, search));
        }
        queryWrapper.eq(ObjectNull.isNotNull(readStatus), UserMessageLog::getReadStatus, readStatus);
        userLogService.page(page, queryWrapper);
        page.getRecords().forEach(e -> ((UserMessageLog) e).setContent(HtmlUtil.cleanHtmlTag(((UserMessageLog) e).getContent()).replaceAll("\n", "")));
        return R.ok(page);
    }

    @Log
    @ApiOperation("查询某一条消息")
    @GetMapping("/{id}")
    public R<UserMessageLog> by(@PathVariable String id) {
        String userId = UserCurrentUtils.getUserId();
        String headImg = UserCurrentUtils.getCurrentUser().getHeadImg();
        UserMessageLog one = userLogService.getOne(Wrappers.query(new UserMessageLog().setUserId(userId).setId(id)));
        one.setHeadImg(headImg);
        return R.ok(one);
    }

    @Log
    @ApiOperation("隐藏消息")
    @DeleteMapping("/hide/{id}")
    public R hide(@PathVariable("id") String id) {
        String userId = UserCurrentUtils.getCurrentUser().getId();
        userLogService.updateById(new UserMessageLog().setHide(true).setId(id).setUserId(userId));
        return R.ok();
    }

    @Log
    @ApiOperation("标记已读")
    @PutMapping("/read/{id}")
    public R read(@PathVariable("id") String id) {
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
