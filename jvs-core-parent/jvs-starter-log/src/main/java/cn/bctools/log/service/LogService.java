package cn.bctools.log.service;

import cn.bctools.log.po.LogPo;
import org.springframework.scheduling.annotation.Async;

/**
 * @author My_gj
 */
public interface LogService {

    /**
     * 注解执行日志对象
     *
     * @param logPo
     */
    void notice(LogPo logPo);

}
