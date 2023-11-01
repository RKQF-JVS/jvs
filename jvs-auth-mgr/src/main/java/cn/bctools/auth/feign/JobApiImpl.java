package cn.bctools.auth.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.api.api.AuthJobServiceApi;
import cn.bctools.auth.api.api.AuthRoleServiceApi;
import cn.bctools.auth.api.dto.SysJobDto;
import cn.bctools.auth.api.dto.SysRoleDto;
import cn.bctools.auth.entity.Job;
import cn.bctools.auth.entity.Role;
import cn.bctools.auth.service.JobService;
import cn.bctools.auth.service.RoleService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 岗位相关接口
 *
 * @Author: GuoZi
 */
@RequestMapping
@RestController
@AllArgsConstructor
public class JobApiImpl implements AuthJobServiceApi {

    JobService jobService;

    @Override
    public R<List<SysJobDto>> getAll() {
        List<Job> list = jobService.list(Wrappers.<Job>lambdaQuery().select(Job::getId, Job::getName));
        return R.ok(BeanCopyUtil.copys(list, SysJobDto.class));
    }

    @Override
    public R<SysJobDto> getById(String id) {
        Job job = jobService.getOne(Wrappers.<Job>lambdaQuery()
                .select(Job::getId, Job::getName)
                .eq(Job::getId, id));
        return R.ok(BeanCopyUtil.copy(job, SysJobDto.class));
    }

    @Override
    public R<List<SysJobDto>> getByIds(List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return R.ok(Collections.emptyList());
        }
        List<Job> list = jobService.list(Wrappers.<Job>lambdaQuery()
                .select(Job::getId, Job::getName)
                .in(Job::getId, ids));
        return R.ok(BeanCopyUtil.copys(list, SysJobDto.class));
    }

}
