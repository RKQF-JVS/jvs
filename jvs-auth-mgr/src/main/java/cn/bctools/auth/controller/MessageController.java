package cn.bctools.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.Message;
import cn.bctools.auth.service.MessageService;
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
@RequestMapping("message")
@AllArgsConstructor
@Api(tags = "消息管理")
public class MessageController {

    MessageService messageService;

    @Log
    @ApiOperation("消息列表")
    @GetMapping("/page")
    public R<Page<Message>> page(Page page, Message message) {
        messageService.page(page, Wrappers.<Message>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(message.getSendType()), Message::getSendType, message.getSendType())
                //类型完全匹配
                .eq(ObjectUtil.isNotEmpty(message.getSendMessageType()), Message::getSendMessageType, message.getSendMessageType())
                //收件人模糊查询
                .like(ObjectUtil.isNotEmpty(message.getRecipients()), Message::getRecipients, message.getRecipients())
                .orderByDesc(Message::getCreateTime));
        return R.ok(page);
    }

    @Log
    @ApiOperation("新增")
    @PostMapping
    public R save(@RequestBody Message log) {
        String username = UserCurrentUtils.getCurrentUser().getRealName();
        log.setSource(username);
        boolean save = messageService.save(log);
        return R.ok(save);
    }

    @Log
    @ApiOperation("发送")
    @GetMapping("/send/{id}")
    public R send(@PathVariable String id) {
        messageService.sendById(id);
        return R.ok();
    }

    @Log
    @ApiOperation("重发")
    @GetMapping("/retry/{id}")
    public R retry(@PathVariable String id) {
        messageService.sendById(id);
        return R.ok();
    }

    @Log
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable String id) {
        messageService.removeById(id);
        return R.ok();
    }

    @Log
    @ApiOperation("修改")
    @PutMapping("/edit")
    public R edit(@RequestBody Message message) {
        String username = UserCurrentUtils.getCurrentUser().getRealName();
        message.setSource(username);
        messageService.updateById(message);
        return R.ok();
    }

}
