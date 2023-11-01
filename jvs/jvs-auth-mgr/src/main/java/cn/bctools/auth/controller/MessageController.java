package cn.bctools.auth.controller;

import cn.bctools.auth.entity.Message;
import cn.bctools.auth.receiver.RabbitMqReceiver;
import cn.bctools.auth.service.MessageService;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 
 */
@Slf4j
@RestController
@RequestMapping("message")
@AllArgsConstructor
@Api(tags = "消息管理")
public class MessageController {

    MessageService messageService;
    RabbitTemplate rabbitTemplate;
    RabbitMqReceiver rabbitMqReceiver;
    public static final String KEY_MSG_SEND_INTERIOR = "msg-send-interior";
    public static final String QUEUE_MSG_SEND_INTERIOR = "queue-" + KEY_MSG_SEND_INTERIOR;
    public static final String EXCHANGE_MSG_SEND = "exchange-msg-send";

    @Bean(EXCHANGE_MSG_SEND)
    public Exchange exchangeMsgSend() {
        return ExchangeBuilder.topicExchange(EXCHANGE_MSG_SEND)
                .durable(true)
                .build();
    }

    @Bean
    public Queue timeLimitQueue() {
        return QueueBuilder.durable(QUEUE_MSG_SEND_INTERIOR).build();
    }

    @Log
    @ApiOperation("消息列表")
    @GetMapping("/page")
    public R<Page<Message>> page(Page page, Message message) {
        messageService.page(page, Wrappers.<Message>lambdaQuery()
                //屏蔽内容
                .select(Message.class, e -> !e.getProperty().equals("content"))
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
    @ApiOperation("新增")
    @GetMapping("/{id}")
    public R getById(@PathVariable String id) {
        Message byId = messageService.getById(id);
        return R.ok(byId);
    }

    @Log
    @ApiOperation("发送")
    @GetMapping("/send/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R send(@PathVariable String id) {
        Message message = messageService.sendById(id);
        try {
            rabbitMqReceiver.send(message);
            message.setStatus(1);
            messageService.updateById(message);
        } catch (Exception e) {
            message.setStatus(0);
            messageService.updateById(message);
            return R.failed("发送失败");
        }
        return R.ok();
    }

    @Log
    @ApiOperation("重发")
    @GetMapping("/retry/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R retry(@PathVariable String id) {
        Message message = messageService.sendById(id);
        try {
            rabbitMqReceiver.send(message);
            message.setStatus(1);
            messageService.updateById(message);
        } catch (Exception e) {
            message.setStatus(0);
            messageService.updateById(message);
            return R.failed("发送失败");
        }
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
