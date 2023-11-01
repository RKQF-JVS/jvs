package cn.bctools.mongodb.core.annotation;

import cn.bctools.mongodb.core.ConditionsAnnotationHandler;

import java.lang.annotation.*;


/**
 * 条件注解,标明该注解是用来构造条件的,${@link ConditionsAnnotationHandler}
 * 如果Field同时标注了 ${@link OperatorAnnotation} 该注解的优先级更高
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionsAnnotation {
}
