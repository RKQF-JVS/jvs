package cn.bctools.mongodb.core;

import cn.bctools.mongodb.core.annotation.ConditionsAnnotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class DefaultConditionsAnnotationHandlerRegister implements ConditionsAnnotationHandlerRegister {
    protected final Map<Class<? extends Annotation>, ConditionsAnnotationHandler> annotationHandlerMap = new HashMap<>();

    @Override
    public void registerHandler(Class<? extends Annotation> annotation, ConditionsAnnotationHandler conditionsAnnotationHandler) {
        if (!annotation.isAnnotationPresent(ConditionsAnnotation.class)) {
            throw new IllegalStateException("annotation  : " + annotation.getName() + " no label ConditionsAnnotation.class");
        }
        annotationHandlerMap.put(annotation, conditionsAnnotationHandler);
    }

    @Override
    public Map<Class<? extends Annotation>, ConditionsAnnotationHandler> getAllRegisteredHandler() {
        Map<Class<? extends Annotation>, ConditionsAnnotationHandler> copyAnnotationHandlerMap = new HashMap<>();
        copyAnnotationHandlerMap.putAll(this.annotationHandlerMap);
        return copyAnnotationHandlerMap;
    }

    @Override
    public ConditionsAnnotationHandler getHandler(Class<? extends Annotation> annotation) {
        return annotationHandlerMap.get(annotation);
    }

}
