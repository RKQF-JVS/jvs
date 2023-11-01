package cn.bctools.message.push.dto;


/**
 * RabbitMQ 前缀管理
 */
public class RabbitMQPrefixManager {

    /**
     * 队列前缀
     */
    public static final String QUEUE_PREFIX = "inside-notification-queue";
    /**
     * 路由前缀
     */
    public static final String ROUTE_PREFIX = "inside-notification-router";
    /**
     * 交换机前缀 默认交换机名称
     */
    public static final String EXCHANGE_PREFIX = "inside-notification-exchange";
}
