package com.xxl.job.core.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@EnableAutoConfiguration
public class XxlJobConfig {

    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);
    /**
     * xxl-job管理地址
     */
    @Value("${xxl.job.admin.addresses:http://xxl-job:9090/xxl-job-admin}")
    private String adminAddresses;
    /**
     * 默认由加入的项目名
     */
    @Value("${xxl.job.executor.appname:${spring.application.name}}")
    private String appName;
    /**
     * 自动获取ip地址
     */
    @Value("${xxl.job.executor.ip:}")
    private String ip;
    /**
     * 默认随机端口
     */
    @Value("${xxl.job.executor.port:${random.int[20000,22000]}}")
    private int port;
    /**
     * 默认为空即可
     */
    @Value("${xxl.job.accessToken:qNAMzjEUPoqjaOBgaGMUWQUud2GNoqW7}")
    private String accessToken;
    /**
     * 日志保存地址
     */
    @Value("${xxl.job.executor.logpath:log/xxl-job/jobhandler-${spring.application.name}}")
    private String logPath;
    /**
     * 默认不过期
     */
    @Value("${xxl.job.executor.logretentiondays:-1}")
    private int logRetentionDays;

    @Bean
//            (initMethod = "start", destroyMethod = "destroy")
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses.trim());
        xxlJobSpringExecutor.setIp(ip);
        logger.info("执行器执行的端口为:{}", port);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAccessToken(accessToken.trim());
        xxlJobSpringExecutor.setLogPath(logPath.trim());
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     *
     *      1、引入依赖：
     *          <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-commons</artifactId>
     *             <version>${version}</version>
     *         </dependency>
     *
     *      2、配置文件，或者容器启动变量
     *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     *
     *      3、获取IP
     *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */


}
