package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.Job;
import cn.bctools.auth.mapper.JobMapper;
import cn.bctools.auth.service.JobService;
import cn.bctools.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 岗位服务
 *
 * @author auto
 */
@Slf4j
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Override
    public Job checkId(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            throw new BusinessException("岗位id为空, 操作失败");
        }
        Job job = this.getById(jobId);
        if (Objects.isNull(job)) {
            log.error("该岗位不存在, 岗位id: {}", jobId);
            throw new BusinessException("该岗位不存在");
        }
        return job;
    }

    @Override
    public void checkJobName(String jobName) {
        this.checkJobName(jobName, null);
    }

    @Override
    public void checkJobName(String jobName, String excludeJobId) {
        if (StringUtils.isBlank(jobName)) {
            throw new BusinessException("岗位名称为空");
        }
        int count = this.count(Wrappers.<Job>lambdaQuery()
                .eq(Job::getName, jobName)
                .ne(StringUtils.isNotBlank(excludeJobId), Job::getId, excludeJobId));
        if (count > 0) {
            throw new BusinessException("岗位名称已存在");
        }
    }

}
