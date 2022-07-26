//package cn.bctools.auth.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import cn.bctools.auth.entity.Message;
//import cn.bctools.auth.entity.enums.MessageStatusEnum;
//import cn.bctools.auth.mapper.MessageMapper;
//import cn.bctools.auth.service.MessageService;
//import cn.bctools.common.exception.BusinessException;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.ExchangeBuilder;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * @author guojing
// */
//@Slf4j
//@Service
//@AllArgsConstructor
//public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
//
//    RabbitTemplate rabbitTemplate;
//
//    public static final String KEY_MSG_SEND_INTERIOR = "msg-send-interior";
//    public static final String QUEUE_MSG_SEND_INTERIOR = "queue-" + KEY_MSG_SEND_INTERIOR;
//    public static final String EXCHANGE_MSG_SEND = "exchange-msg-send";
//
//    @Bean(EXCHANGE_MSG_SEND)
//    public Exchange exchangeMsgSend() {
//        return ExchangeBuilder.topicExchange(EXCHANGE_MSG_SEND)
//                .durable(true)
//                .build();
//    }
//
//    /**
//     * 发送消息
//     */
//    public void send(String body) {
//        rabbitTemplate.convertAndSend(EXCHANGE_MSG_SEND, QUEUE_MSG_SEND_INTERIOR, body);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void sendById(String id) {
//        // 查询消息
//        Message message = this.getById(id);
//        if (null == message) {
//            throw new BusinessException("该消息不存在(id:" + id + ")");
//        }
//        message.setStatus(MessageStatusEnum.SENDING.getCode());
//        this.updateById(message);
//        send(JSONObject.toJSONString(message));
//    }
//
//}
