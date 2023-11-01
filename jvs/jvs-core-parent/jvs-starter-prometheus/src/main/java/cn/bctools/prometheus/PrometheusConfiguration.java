package cn.bctools.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @author Administrator
 */
@Slf4j
public class PrometheusConfiguration implements CommandLineRunner {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> appMetricsCommonTags() {
        return registry -> registry.config().commonTags("application", applicationName);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("prometheus startup successfully! ");
    }

}