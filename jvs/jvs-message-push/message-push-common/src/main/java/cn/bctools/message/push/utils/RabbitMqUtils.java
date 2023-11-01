package cn.bctools.message.push.utils;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.message.push.dto.RabbitMQPrefixManager;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * rabbitMq工具
 */
@AllArgsConstructor
@Component
public class RabbitMqUtils {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    public static final String QUEUE_PREFIX = RabbitMQPrefixManager.QUEUE_PREFIX;
    public static final String ROUTE_PREFIX = RabbitMQPrefixManager.ROUTE_PREFIX;

    public boolean checkQueueExist(String queueName){
        queueName = QUEUE_PREFIX+"-"+ queueName;
        Properties queueProperties = amqpAdmin.getQueueProperties(queueName);
        return queueProperties != null;
    }

    /**
     * 创建交换机
     */
    public void createExchange(){
        amqpAdmin.declareExchange(DefinedExchange.DEFAULT);
    }

    /**
     * 绑定队列和交换机
     * @param clientCode 终端唯一标识
     * @param destinationType 目标类型
     * @param exchangeName 交换机名称
     * @param declarableParams 创建参数
     */
    public void createBinding(String clientCode, Binding.DestinationType destinationType, String exchangeName, @Nullable Map<String, Object> declarableParams){
        if(clientCode==null){
            throw new BusinessException("队列名称不能为空");
        }
        String queueName = QUEUE_PREFIX+"-"+clientCode;
        String routerKey = ROUTE_PREFIX+"-"+clientCode;
        if(exchangeName==null){
            throw new BusinessException("交换机名称不能为空");
        }
        amqpAdmin.declareBinding(new Binding(queueName,destinationType,exchangeName,routerKey,declarableParams));
    }

    /**
     * 创建队列
     * @param queueName 队列名称
     */
    public void createQueue(String queueName){
        if(queueName==null){
            throw new BusinessException("队列名称不能为空");
        }
        queueName = QUEUE_PREFIX+"-"+queueName;
        amqpAdmin.declareQueue(new Queue(queueName));
    }

    /**
     * 推消息到rabbitmq
     * @param dto
     */
    public void send(InsideNotificationDto dto){
        String routingKey = ROUTE_PREFIX+"-"+dto.getClientCode();
        rabbitTemplate.convertAndSend(DefinedExchange.EXCHANGE_NAME,routingKey, JSONUtil.toJsonStr(dto));
    }

}
