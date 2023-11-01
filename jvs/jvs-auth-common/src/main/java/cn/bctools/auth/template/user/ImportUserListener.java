package cn.bctools.auth.template.user;

import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.auth.api.dto.SysJobDto;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.Job;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SexTypeEnum;
import cn.bctools.auth.service.*;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.IdGenerator;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.gateway.entity.TenantPo;
import cn.hutool.core.lang.Validator;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ZhuXiaoKang
 * @Description: 导入用户Excel解析处理
 */
public class ImportUserListener implements ReadListener<UserExcelTemplate> {

    // 用户信息
    private UserDto userDto;
    // 所有部门
    private List<SysDeptDto> deptTree;
    // 获取所有岗位
    private List<SysJobDto> sysJobDtos;

    private final DeptService deptService = SpringContextUtil.getBean(DeptService.class);
    private final JobService jobService = SpringContextUtil.getBean(JobService.class);
    private final UserService userService = SpringContextUtil.getBean(UserService.class);
    private final UserTenantService userTenantService = SpringContextUtil.getBean(UserTenantService.class);
    private final SmsEmailComponent smsComponent = SpringContextUtil.getBean(SmsEmailComponent.class);
    private final TenantService tenantService = SpringContextUtil.getBean(TenantService.class);

    public ImportUserListener(UserDto userDto, List<SysDeptDto> deptTree, List<SysJobDto> sysJobDtos) {
        this.deptTree = deptTree;
        this.sysJobDtos = sysJobDtos;
        this.userDto = userDto;
    }

    /**
     * 单次缓存的数据量
     */
    private static final int BATCH_COUNT = 1000;

    /**
     * 缓存数据
     */
    private List<UserExcelTemplate> userExcelDatas = new ArrayList<>();

    /**
     * 部门路径缓存
     * Map<部门全路径, Map<部门名称, 部门id>>
     */
    private Map<String, Map<String, String>> deptPathCache = new HashMap<>();

    /**
     * 需要自动创建的部门信息
     */
    private List<SysDeptDto> addDepts = new ArrayList<>();

    /**
     * 岗位缓存
     * Map<岗位名称, 岗位id>>
     */
    private Map<String, String> jobCache = new HashMap<>();
    /**
     * 需要自动创建的岗位
     */
    private List<SysJobDto> addJobs = new ArrayList<>();

    private TenantPo tenantPo = null;

    private String errorMsg;


    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {

    }

    /**
     * 这里会一行行的返回头
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        // 不处理头
    }

    /**
     * 这个每一条数据解析都会来调用
     * @param data
     * @param context
     */
    @Override
    public void invoke(UserExcelTemplate data, AnalysisContext context) {
        // 性别转换
        data.setSex(SexTypeEnum.getByDesc(data.getGender()));
        // 校验数据
        checkData(data);
        // 加入缓存
        userExcelDatas.add(data);
        // 分批处理
        if (userExcelDatas.size() >= BATCH_COUNT) {
            checkErrorMsg();
            // 清理缓存
            clear();
            convertData();
            // 当前批次处理完毕，清空缓存
            userExcelDatas = new ArrayList<>();
        }
    }

    /**
     * 额外信息
     * @param extra
     * @param context
     */
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        // 不处理额外信息
    }

    /**
     * 所有数据解析完成了 都会来调用
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        checkErrorMsg();
        // 清理缓存
        clear();
        convertData();
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return true;
    }

    /**
     * 数据校验
     * @param data
     */
    private void checkData(UserExcelTemplate data) {
        if (StringUtils.isBlank(data.getRealName())) {
            errorMsg = "姓名不能为空";
        }
        if (StringUtils.isNotBlank(data.getPhone()) && Boolean.FALSE.equals(Validator.isMobile(data.getPhone()))) {
            errorMsg = "手机号格式错误";
        }
        if (StringUtils.isNotBlank(data.getEmail()) && Boolean.FALSE.equals(Validator.isEmail(data.getEmail()))) {
            errorMsg = "邮箱格式错误";
        }
        if (StringUtils.isNotBlank(data.getLevel()) && Boolean.FALSE.equals(Validator.isNumber(data.getLevel()))) {
            errorMsg = "账号等级只能是数字";
        }
    }

    /**
     * 校验是否有异常信息，有则抛出
     */
    private void checkErrorMsg() {
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new BusinessException(errorMsg);
        }
    }

    /**
     * 清理缓存
     */
    private void clear() {
        // 清理新增的部门信息
        addDepts = new ArrayList<>();
        // 清理新增的岗位信息
        addJobs = new ArrayList<>();
    }

    /**
     * 数据转换
     */
    private void convertData() {
        if (CollectionUtils.isEmpty(userExcelDatas)) {
            return;
        }
        // 得到部门路径集合
        Set<String> deptNamePaths = userExcelDatas.stream().filter(u -> StringUtils.isNotBlank(u.getDeptName())).map(UserExcelTemplate::getDeptName).collect(Collectors.toSet());
        deptNamePaths.forEach(deptNamePath -> {
            if (Boolean.FALSE.equals(deptPathCache.containsKey(deptNamePath))) {
                String[] deptNameArr = deptNamePath.split("/");
                parseDept(deptNamePath, userDto.getTenantId(), deptTree, deptTree, null, deptNameArr, 0, deptNameArr.length);
            }
        });
        // 得到岗位集合
        Set<String> jobNames = userExcelDatas.stream().filter(u -> StringUtils.isNotBlank(u.getJobName())).map(UserExcelTemplate::getJobName).collect(Collectors.toSet());
        jobNames.forEach(jobName -> {
            if (Boolean.FALSE.equals(jobCache.containsKey(jobName))) {
                parseJob(sysJobDtos ,jobName);
            }
        });

        // 保存
        save();
    }

    /**
     * 解析部门
     * @param deptPath 部门全路径名
     * @param rootParentId 最上级父节点（租户id）
     * @param deptTree 所有部门
     * @param deptDtos 用以迭代查询的部门信息
     * @param parentIds 父级部门id集合
     * @param deptNames 部门名集合（由部门全路径名分解）
     * @param i 部门名集合中当前部门名下标
     * @param size 部门名集合中总部门名数量
     */
    private void parseDept(String deptPath, String rootParentId, List<SysDeptDto> deptTree, List<SysDeptDto> deptDtos, Set<String> parentIds, String[] deptNames, int i, int size) {
        String deptName = deptNames[i];
        i++;
        List<SysDeptDto> sysDeptDtos = deptDtos.stream().filter(dept -> dept.getName().equals(deptName)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sysDeptDtos)) {
            // 得到父级id集合
            if (CollectionUtils.isEmpty(parentIds)) {
                parentIds = deptDtos.stream().map(SysDeptDto::getParentId).collect(Collectors.toSet());
            }
            buildDept(rootParentId, deptTree, parentIds, deptName);
            i = 0;
            parseDept(deptPath,null, deptTree, deptTree, parentIds, deptNames, i, size);
        } else {
            if (i == size) {
                Map<String, String> dept = new HashMap<>();
                dept.put(deptName, sysDeptDtos.get(0).getId());
                deptPathCache.put(deptPath, dept);
                return;
            } else {
                // 获取匹配的所有下级部门
                List<SysDeptDto> nextSysDeptDtos = new ArrayList<>();
                sysDeptDtos.forEach(d -> {
                    if (CollectionUtils.isNotEmpty(d.getChildList())) {
                        nextSysDeptDtos.addAll(d.getChildList());
                    }
                });
                parentIds = sysDeptDtos.stream().map(SysDeptDto::getId).collect(Collectors.toSet());
                parseDept(deptPath,null, deptTree, nextSysDeptDtos, parentIds, deptNames, i, size);
            }
        }
    }

    /**
     * 构造新部门节点
     * @param rootParentId 最上级父节点（租户id）
     * @param deptTree 所有部门
     * @param parentIds 父级节点id集合
     * @param deptName 部门名
     */
    private void buildDept(String rootParentId, List<SysDeptDto> deptTree, Set<String> parentIds, String deptName) {
        boolean addDept = Boolean.FALSE;
        List<SysDeptDto> depts = deptTree.stream().filter(d -> parentIds.contains(d.getId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(depts)) {
            for (SysDeptDto dept : depts) {
                SysDeptDto sysDeptDto = createDeptDto(deptName, dept.getId());
                if (CollectionUtils.isEmpty(dept.getChildList())) {
                    dept.setChildList(new ArrayList(){{add(sysDeptDto);}});
                } else {
                    dept.getChildList().add(sysDeptDto);
                }
                addDepts.add(sysDeptDto);
                addDept = Boolean.TRUE;
            }
        } else {
            if (parentIds.contains(rootParentId)) {
                SysDeptDto sysDeptDto = createDeptDto(deptName, rootParentId);
                deptTree.add(sysDeptDto);
                addDepts.add(sysDeptDto);
                addDept = Boolean.TRUE;
            }
        }

        if (Boolean.FALSE.equals(addDept)) {
            deptTree.forEach(d -> {
                buildDept(rootParentId, Optional.ofNullable(d.getChildList()).orElse(new ArrayList<>()), parentIds , deptName);
            });
        }
    }

    /**
     * 解析岗位
     * @param sysJobDtos 所有岗位
     * @param jobName 岗位名
     */
    private void parseJob(List<SysJobDto> sysJobDtos, String jobName) {
        String jobId = sysJobDtos.stream().filter(j -> j.getName().equals(jobName)).map(SysJobDto::getId).collect(Collectors.joining());
        if (StringUtils.isBlank(jobId)) {
            SysJobDto sysJobDto = new SysJobDto().setName(jobName).setId(IdWorker.get32UUID());
            sysJobDtos.add(sysJobDto);
            addJobs.add(sysJobDto);
            jobId = sysJobDto.getId();
        }
        jobCache.put(jobName, jobId);
    }

    /**
     * 创建系统部门信息对象
     * @param deptName 部门名
     * @param parentId 父级部门id
     * @return 系统部门信息
     */
    private SysDeptDto createDeptDto(String deptName, String parentId) {
        return new SysDeptDto()
                .setId(IdWorker.get32UUID())
                .setName(deptName)
                .setParentId(parentId);
    }

    /**
     * 保存
     */
    private void save() {
        // 保存岗位信息
        saveDept();
        // 保存部门信息
        saveJob();
        // 保存用户信息
        saveUser();
    }

    /**
     * 保存部门
     */
    private void saveDept() {
        if (CollectionUtils.isEmpty(addDepts)) {
            return;
        }
        List<Dept> addDeptList = BeanCopyUtil.copys(addDepts, Dept.class);
        deptService.saveBatch(addDeptList);
    }

    /**
     * 保存岗位
     */
    private void saveJob() {
        if (CollectionUtils.isEmpty(addJobs)) {
            return;
        }
        List<Job> addJobList = BeanCopyUtil.copys(addJobs, Job.class);
        jobService.saveBatch(addJobList);
    }

    /**
     * 保存用户
     */
    private void saveUser() {
        if (CollectionUtils.isEmpty(userExcelDatas)) {
            return;
        }
        List<User> users = new ArrayList<>();
        List<UserTenant> userTenants = new ArrayList<>();
        userExcelDatas.forEach(u -> {
            User user = BeanCopyUtil.copy(u, User.class);
            user.setAccountName(IdGenerator.getIdStr(36));
            user.setId(IdWorker.get32UUID());
            UserTenant userTenant = BeanCopyUtil.copy(u, UserTenant.class);
            userTenant.setUserId(user.getId());
            convertUserTenant(userTenant);
            users.add(user);
            userTenants.add(userTenant);

        });

        // 校验手机号是否已存在
        Map<String, Long> phoneGroup = users.stream().filter(u -> StringUtils.isNotBlank(u.getPhone())).collect(Collectors.groupingBy(User::getPhone, Collectors.counting()));
        long repeatPhoneNum = phoneGroup.values().stream().filter(v -> v > 1).count();
        if (repeatPhoneNum > 0) {
            throw new BusinessException("有已存在的手机号");
        }
        Set<String> phones = users.stream().map(User::getPhone).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(phones)) {
            int count = userService.count(Wrappers.<User>lambdaQuery().in(User::getPhone, phones));
            if (count > 0) {
                throw new BusinessException("有已存在的手机号");
            }
        }

        // 保存
        userService.saveBatch(users);
        userTenantService.saveBatch(userTenants);
        // 发送通知
        sendNotify(users);
    }

    /**
     * 租户用户信息转换
     * @param userTenant
     */
    private void convertUserTenant(UserTenant userTenant) {
        convertDept(userTenant);
        convertJob(userTenant);
    }

    /**
     * 部门信息转换
     * @param userTenant
     */
    private void convertDept(UserTenant userTenant) {
        if (StringUtils.isBlank(userTenant.getDeptName())) {
            return;
        }
        Map<String, String> deptCache = deptPathCache.get(userTenant.getDeptName());
        String deptName = null;
        String deptId = null;
        for (Map.Entry<String, String> entry : deptCache.entrySet()) {
            deptName = entry.getKey();
            deptId = entry.getValue();
        }
        userTenant.setDeptName(deptName);
        userTenant.setDeptId(deptId);
    }

    /**
     * 岗位信息转换
     * @param userTenant
     */
    private void convertJob(UserTenant userTenant) {
        if (StringUtils.isBlank(userTenant.getJobName())) {
            return;
        }
        userTenant.setJobId(jobCache.get(userTenant.getJobName()));
    }

    /**
     * 发送通知
     */
    private void sendNotify(List<User> users) {
        // 发送通知
        if (Objects.isNull(tenantPo)) {
            tenantPo = tenantService.getById(userDto.getTenantId());
        }
        users.forEach(user -> smsComponent.sendUserInvite(userDto, user, tenantPo));
    }


}
