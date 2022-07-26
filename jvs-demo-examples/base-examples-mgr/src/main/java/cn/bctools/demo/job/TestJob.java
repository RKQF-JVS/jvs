package cn.bctools.demo.job;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class TestJob {

    @XxlJob("test")
    public void test() {
        String jobParam = XxlJobContext.getXxlJobContext().getJobParam();
        System.out.println(jobParam);
    }
}
