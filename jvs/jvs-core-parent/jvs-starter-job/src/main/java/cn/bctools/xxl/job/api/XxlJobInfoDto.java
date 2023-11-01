package cn.bctools.xxl.job.api;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 
 * @describe 接口生成定时任务
 */
@Data
@Accessors(chain = true)
public class XxlJobInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**任务执行CRON表达式 【base on quartz】*/
    private String scheduleConf;
    /**任务描述*/
    private String jobDesc;
    /**executorHandler名称*/
    private String executorHandler;
    /** 负责人*/
    private String author;
    /**报警邮件*/
    private String alarmEmail;
    /** 执行器，任务参数*/
    private String executorParam;
}
