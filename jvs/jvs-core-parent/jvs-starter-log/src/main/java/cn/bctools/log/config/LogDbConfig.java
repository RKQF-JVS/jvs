package cn.bctools.log.config;

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.dingding.DingSendUtils;
import cn.bctools.log.event.LogApplicationEvent;
import cn.bctools.log.mapper.SysLogDao;
import cn.bctools.log.po.LogPo;
import cn.bctools.log.service.LogService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@Configuration
public class LogDbConfig {

    @Value("${ding.log:false}")
    private Boolean log;
    @Value("${ding.secret:}")
    private String secret;
    @Value("${ding.url:}")
    private String url;
    @Value("${ding.phones:}")
    private List<String> phones = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean
    LogService logService(SysLogDao sysLogDao) {
        return logPo -> sysLogDao.insert(logPo);
    }

    //异步处理防止影响主流程
    @EventListener
    @Async
    public void onApplicationEvent(LogApplicationEvent event) {
        LogPo logPo = event.getLogPo();

        if (log && ObjectNull.isNotNull(logPo.getExceptionMessage())) {
            //发送通知
            SpringContextUtil.getBean(DingSendUtils.class).sendMessage(url, secret, logPo.getExceptionMessage(), phones);
        }
        SpringContextUtil.getBean(LogService.class).notice(logPo);
    }

}
