package cn.bctools.xxl.job.api;

import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author guojing
 */
@FeignClient(name = "jvs-apply-job-admin", path = "/xxl-job-admin")
public interface XxlAdminApi {

    /**
     * 删除一个定时任务
     *
     * @param id 使用定时任务的ID值
     * @throws Exception 删除一个定时任务失败
     * @author: guojing
     */
    @DeleteMapping("/job/api/{id}")
    R<Boolean> delete(@PathVariable("id") int id, @RequestHeader("XXL-JOB-ACCESS-TOKEN") String token) throws Exception;

    /**
     * 生成一个新的定时任务
     * 或直接更新一个新的定时任务，逻辑执行器那边添加或修改时，可直接调用此方法即可完成定时任务的调用，调用的定时任务Handler 名为 RuleTask   参数自定义
     *
     * @param xxlJobInfoDto 定时任务的基础参数
     * @throws Exception 修改或保存一个定时任务失败
     * @author: guojing
     */
    @PostMapping("/job/api/save")
    R<Integer> save(@RequestBody XxlJobInfoDto xxlJobInfoDto, @RequestHeader("XXL-JOB-ACCESS-TOKEN") String token, @RequestHeader("jobName") String jobName) throws Exception;

}
