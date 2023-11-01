package cn.bctools.mongodb.core;


import cn.bctools.mongodb.core.annotation.OperatorAnnotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class DefaultOperatorAnnotationHandlerRegister implements OperatorAnnotationHandlerRegister {
    protected final Map<Class<? extends Annotation>, OperatorAnnotationHandler> annotationHandlerMap = new HashMap<>();

    @Override
    public void registerHandler(Class<? extends Annotation> annotation, OperatorAnnotationHandler operatorAnnotationHandler) {
        if (!annotation.isAnnotationPresent(OperatorAnnotation.class)) {
            throw new IllegalStateException("annotation  : " + annotation.getName() + " no label OperatorAnnotation.class");
        }
        annotationHandlerMap.put(annotation, operatorAnnotationHandler);
    }


    @Override
    public Map<Class<? extends Annotation>, OperatorAnnotationHandler> getAllRegisteredHandler() {
        Map<Class<? extends Annotation>, OperatorAnnotationHandler> copyAnnotationHandlerMap = new HashMap<>();
        copyAnnotationHandlerMap.putAll(this.annotationHandlerMap);
        return copyAnnotationHandlerMap;
    }

    @Override
    public OperatorAnnotationHandler getHandler(Class<? extends Annotation> annotation) {
        return annotationHandlerMap.get(annotation);
    }


}
