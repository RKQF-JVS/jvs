package cn.bctools.message.push.handler;

import cn.bctools.message.push.dto.messagePush.BaseMessage;

/**
 * 消息处理器基类
 *
 **/
public abstract class MessageHandler<T extends BaseMessage> {

    /**
     * 实现这个接口来处理消息，再正式调用这个方法之前会处理好需要的参数和需要的配置
     */
    public abstract void handle(T param) throws Exception;

    /**
     * 实现这个接口来处理消息，再正式调用这个方法之前会处理好需要的参数和需要的配置
     */
    public abstract void resend(String pushHisId) throws Exception;

}
