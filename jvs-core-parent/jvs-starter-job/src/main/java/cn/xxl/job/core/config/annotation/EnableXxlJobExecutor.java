package com.xxl.job.core.config.annotation;

import com.xxl.job.core.config.XxlJobConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * @author My_gj
 * 开启执行器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({XxlJobConfig.class})
public @interface EnableXxlJobExecutor {
}
