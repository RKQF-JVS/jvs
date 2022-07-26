package cn.bctools.log.config;

import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.log.event.LogApplicationEvent;
import cn.bctools.log.mapper.SysLogDao;
import cn.bctools.log.po.LogPo;
import cn.bctools.log.service.LogService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Administrator
 */
@Data
@Configuration
public class LogDbConfig {

    @Bean
    @ConditionalOnMissingBean
    LogService logService(SysLogDao sysLogDao) {
        return logPo -> sysLogDao.insert(logPo);
    }

    //异步处理防止影响主流程
    @EventListener
    @Async
    public void onApplicationEvent(LogApplicationEvent event) {
        SpringContextUtil.getBean(LogService.class).notice(event.getLogPo());
    }

}
