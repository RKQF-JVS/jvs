package cn.bctools.message.push.utils;

import cn.bctools.message.push.dto.RabbitMQPrefixManager;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.Map;

public class DefinedExchange extends AbstractExchange {

    public static final String EXCHANGE_NAME = RabbitMQPrefixManager.EXCHANGE_PREFIX;

    public static final DefinedExchange DEFAULT = new DefinedExchange(EXCHANGE_NAME);

    public DefinedExchange(String name) {
        super(name);
    }

    public DefinedExchange(String name, boolean durable, boolean autoDelete) {
        super(name, durable, autoDelete);
    }

    public DefinedExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        super(name, durable, autoDelete, arguments);
    }

    @Override
    public String getType() {
        return ExchangeTypes.DIRECT;
    }
}
