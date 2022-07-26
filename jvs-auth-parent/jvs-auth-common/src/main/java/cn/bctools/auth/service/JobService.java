package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.Job;

/**
 * @author
 */
public interface JobService extends IService<Job> {

    /**
     * 校验岗位id
     *
     * @param jobId 岗位id
     * @return 岗位信息
     */
    Job checkId(String jobId);

    /**
     * 校验岗位名称唯一性
     *
     * @param jobName 岗位名称
     */
    void checkJobName(String jobName);

    /**
     * 校验岗位名称唯一性
     *
     * @param jobName      岗位名称
     * @param excludeJobId 排除的岗位id
     */
    void checkJobName(String jobName, String excludeJobId);

}
