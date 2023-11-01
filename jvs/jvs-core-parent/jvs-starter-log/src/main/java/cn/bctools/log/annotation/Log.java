package cn.bctools.log.annotation;

import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.*;

/**
 * @author My_gj
 * 环绕日志注解
 * 是否记录返回结果,默认记录,目前已经支持的几个数据类型已经不做日志记录了
 * {@linkplain MultipartFile}
 * 目前已经处理过这三类数据类型，不做为入参记录
 * 日志保存对象默认为mongo对象如果需要自定义保存地址，请自行实现bean
 * 如果业务中还存在有其它的日志保存需要手动处理的。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 记录操作类型
     */
    String operationType() default "--";

    /**
     * 描述将其操作日志注解进行数据保存
     */
    String value() default "";

    /**
     * 若返回值数据量过大, 则建议设为false, 否则可能会报{@link OutOfMemoryError}
     * 返回结果不做为日志记录，目前业务中还未出现返回结果不做日志记录的
     */
    boolean back() default true;

    /**
     * 操作日志信息回调，将此日志场景单独存储一份 ，可以直接调用查询使用
     */
    Class<? extends LogCallBack> callBackClass() default LogCallBack.class;
}
