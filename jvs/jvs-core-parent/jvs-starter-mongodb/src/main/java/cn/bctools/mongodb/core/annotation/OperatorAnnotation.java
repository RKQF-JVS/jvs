package cn.bctools.mongodb.core.annotation;

import cn.bctools.mongodb.core.OperatorAnnotationHandler;

import java.lang.annotation.*;

/**
 * 运算符注解,标明该注解是用来构造两个Criteria的运算符,${@link OperatorAnnotationHandler}
 * 如果要弄运算符的话 开始最好是在上一个field标注,结束的话在自己的field上标注
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OperatorAnnotation {

}
