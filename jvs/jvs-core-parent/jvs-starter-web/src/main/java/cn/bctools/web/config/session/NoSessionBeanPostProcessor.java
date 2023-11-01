package cn.bctools.web.config.session;

import io.undertow.servlet.core.ServletExtensionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: ZhuXiaoKang
 * @Description: 初始化undertow之前，增加自定义SessionManager扩展
 */
@Slf4j
@Configuration
public class NoSessionBeanPostProcessor implements BeanPostProcessor {
    private Boolean addNoSessionHandlerExtension = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (Boolean.FALSE.equals(addNoSessionHandlerExtension)) {
            log.debug("添加空session实现的ServletExtensionHolder");
            ServletExtensionHolder.getServletExtensions().add(new NoSessionHandlerExtension());
            addNoSessionHandlerExtension = true;
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
