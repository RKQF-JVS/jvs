package cn.bctools.mongodb.core;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 注册运算符相关注解的处理器注册
 */
public interface OperatorAnnotationHandlerRegister {
    /**
     * Annotation 必须标准OperatorAnnotation注解
     *
     * @param annotation
     * @param operatorAnnotationHandler
     */
    void registerHandler(Class<? extends Annotation> annotation, OperatorAnnotationHandler operatorAnnotationHandler);

    /**
     * 返回的是一个copy,不是原注册Map
     * @return
     */
    Map<Class<? extends Annotation>, OperatorAnnotationHandler> getAllRegisteredHandler();

    OperatorAnnotationHandler getHandler(Class<? extends Annotation> annotation);
}
