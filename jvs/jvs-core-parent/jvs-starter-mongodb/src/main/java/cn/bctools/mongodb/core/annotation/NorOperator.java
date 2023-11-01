package cn.bctools.mongodb.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@OperatorAnnotation
public @interface NorOperator {
}