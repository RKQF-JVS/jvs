package cn.bctools.log.annotation;

import java.lang.annotation.*;

/**
 * 表示不记录这个数据字段
 * 请求的参数会通过异步记录到日志服务器中
 *
 * @author: 
 * @return:
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogIgnore {

}
