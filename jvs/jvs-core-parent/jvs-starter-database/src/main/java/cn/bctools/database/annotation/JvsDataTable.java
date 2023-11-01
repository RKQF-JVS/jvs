package cn.bctools.database.annotation;

import java.lang.annotation.*;

/**
 * 标记为数据权限实体类对象，此注解可以直接和TableName代替
 * 主要给数据表明声明数据描述
 *
 * @author 
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JvsDataTable {

    /**
     * 此数据表的 描述
     */
    String value();

    /**
     * 中文描述,请与数据权限表示描述保持一致。 如果不一致则不能设置成功 如  客户管理
     */
    String desc();

}
