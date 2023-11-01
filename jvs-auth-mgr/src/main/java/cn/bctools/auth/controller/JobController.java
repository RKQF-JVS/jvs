package cn.bctools.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.auth.entity.Job;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.service.JobService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.vo.UserVo;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @ClassName: DeptController
 * @Description: 部门管理(这里用一句话描述这个类的作用)
 */
@Slf4j
@AllArgsConstructor
@Api(value = "岗位管理", tags = "岗位接口")
@RestController
@RequestMapping("/job")
public class JobController {

    JobService jobService;
    UserService userService;
    UserTenantService userTenantService;

    @Log
    @GetMapping("/list")
    @ApiOperation(value = "所有岗位", notes = "所有岗位，然后显示在岗位列表左边")
    public R<List<Job>> list() {
        return R.ok(jobService.list(Wrappers.<Job>lambdaQuery().orderByAsc(Job::getName)));
    }

    @Log
    @ApiOperation(value = "岗位下的用户", notes = "岗位下面所有的用户")
    @GetMapping("/users")
    public R<Page<UserVo>> users(@RequestParam(value = "jobId", required = false) String jobId, Page<UserTenant> page) {
        //中间关系
        if (ObjectNull.isNotNull(jobId)) {
            userTenantService.page(page, Wrappers.<UserTenant>lambdaQuery().eq(UserTenant::getJobId, jobId));
        } else {
            userTenantService.page(page);
        }
        Set<String> userIdSet = page.getRecords().stream().map(UserTenant::getUserId).collect(Collectors.toSet());
        // 可能没有用户
        Page<UserVo> userPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal(), page.isSearchCount());
        if (ObjectUtil.isEmpty(userIdSet)) {
            return R.ok(userPage);
        }
        Map<String, User> userMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        List<UserVo> list = page.getRecords().stream()
                .map(e -> BeanCopyUtil.copy(UserVo.class, userMap.get(e.getUserId()), e))
                .collect(Collectors.toList());
        userPage.setRecords(list);
        return R.ok(userPage);
    }

    @Log
    @PostMapping
    @ApiOperation(value = "添加岗位", notes = "添加一个岗位， 直接显示即可")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> save(@RequestBody @Validated Job job) {
        jobService.checkJobName(job.getName());
        jobService.save(job);
        return R.ok(true, "添加成功");
    }

    @Log
    @PutMapping
    @ApiOperation(value = "修改岗位", notes = "修改一个岗位，只修改基本信息")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> update(@RequestBody @Validated Job job) {
        String jobId = job.getId();
        String jobName = job.getName();
        Job oldJob = jobService.checkId(jobId);
        jobService.checkJobName(jobName, jobId);
        jobService.updateById(job);
        // 同步部门名称
        if (!jobName.equals(oldJob.getName())) {
            userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                    .set(UserTenant::getJobName, jobName)
                    .eq(UserTenant::getJobId, jobId));
        }
        return R.ok(true, "修改成功");
    }

    @Log
    @DeleteMapping("/user/{userId}")
    @ApiOperation(value = "用户移除岗位", notes = "只是移除岗位，并没有其它操作")
    public R<Boolean> deleteUser(@PathVariable String userId) {
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getJobId, null)
                .set(UserTenant::getJobName, null)
                .eq(UserTenant::getUserId, userId));
        return R.ok(true, "移除成功");
    }

    @Log
    @PutMapping("/user/{jobId}")
    @ApiOperation(value = "用户添加到某个岗位", notes = "添加到某个岗位时，只需要岗位ID和选择的哪些用户")
    public R<Boolean> putUser(@RequestBody List<UserTenant> list, @PathVariable String jobId) {
        Job job = jobService.getById(jobId);
        Assert.notNull(job, "该岗位不存在");
        Set<String> userIdSet = list.stream().map(UserTenant::getId).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(userIdSet)) {
            return R.ok(true, "添加成功");
        }
        // 修改用户信息
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getJobId, job.getId())
                .set(UserTenant::getJobName, job.getName())
                .in(UserTenant::getUserId, userIdSet));
        return R.ok(true, "添加成功");
    }

    @Log
    @DeleteMapping("/{jobId}")
    @ApiOperation(value = "删除岗位", notes = "删除岗位后，下面所有用户都将移除此岗位信息")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> job(@PathVariable String jobId) {
        // 删除部门信息
        jobService.removeById(jobId);
        // 移除该部门下的用户
        userTenantService.update(Wrappers.<UserTenant>lambdaUpdate()
                .set(UserTenant::getJobId, null)
                .set(UserTenant::getJobName, null)
                .eq(UserTenant::getJobId, jobId));
        return R.ok(true, "删除成功");
    }

}
