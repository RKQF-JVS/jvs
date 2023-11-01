package cn.bctools.dynamic.config;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import cn.bctools.dynamic.getter.DefaultDynamicDataSourceGetter;
import cn.bctools.dynamic.getter.IDynamicDataSourceGetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 动态数据源自动配置
 *
 * @author auto
 */
@Slf4j
@AutoConfigureAfter(DynamicDataSourceAutoConfiguration.class)
public class DynamicDataConfig {

    @Bean
    @ConditionalOnMissingBean
    public IDynamicDataSourceGetter dataSourceGetter() {
        log.info("[dynamic-data] 使用默认的数据源信息获取类: {}", DefaultDynamicDataSourceGetter.class.getName());
        return new DefaultDynamicDataSourceGetter();
    }

}
