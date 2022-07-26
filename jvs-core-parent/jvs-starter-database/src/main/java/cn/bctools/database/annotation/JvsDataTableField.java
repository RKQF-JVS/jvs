package cn.bctools.database.annotation;

import cn.bctools.database.interfaces.DataEnum;

import java.lang.annotation.*;

/**
 * 数据表是否支持自定义数据权限操作
 * <p>
 * 注解添加在po对象的属性上(该属性必须是枚举类, 而且是{@link DataEnum}的实现类)。可对属性进行数据权限自定义控制
 *
 * @author guojing
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JvsDataTableField {

    /**
     * 数据库字段名
     */
    String name();

    /**
     * 这个字段的解释
     */
    String desc();

}
