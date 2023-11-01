package cn.bctools.mongodb.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ConditionsAnnotation
public @interface Eq {
    // 字段的名称
    String value() default "";

}
