package cn.bctools.auth.service.impl;

import cn.bctools.auth.api.api.AuthDeptServiceApi;
import cn.bctools.auth.api.api.AuthJobServiceApi;
import cn.bctools.auth.api.dto.SysDeptDto;
import cn.bctools.auth.api.dto.SysJobDto;
import cn.bctools.auth.entity.*;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.auth.mapper.*;
import cn.bctools.auth.service.TenantService;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserService;
import cn.bctools.auth.service.UserTenantService;
import cn.bctools.auth.template.user.ImportUserListener;
import cn.bctools.auth.template.user.UserExcelTemplate;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.web.excel.ArrayListConvert;
import cn.bctools.web.excel.LocalDateTimeConvert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 用户服务
 *
 * @author
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    UserMapper userMapper;
    UserRoleMapper userRoleMapper;
    UserTenantMapper userTenantMapper;
    UserExtensionMapper userExtensionMapper;
    UserTenantService userTenantService;
    UserExtensionService userExtensionService;
    DeptMapper deptMapper;
    JobMapper jobMapper;
    ArrayListConvert arrayListConvert;
    LocalDateTimeConvert localDateTimeConvert;
    AuthDeptServiceApi deptServiceApi;
    AuthJobServiceApi authJobServiceApi;
    TenantService tenantService;
    PasswordEncoder passwordEncoder;

    public static final String FILE_TYPE = ".xlsx";
    /**
     * 批量同步用户单批次数量
     */
    private static final Integer SYNC_BATCH_SIZE = 500;

    @Override
    public User saveUser(User user, UserTenant userTenant) {
        String phone = user.getPhone();
        int count = this.count(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
        if (count > 0) {
            throw new BusinessException("手机号已存在");
        }
        if (ObjectNull.isNotNull(user.getAccountName())) {
            int vv = this.count(Wrappers.<User>lambdaQuery().eq(User::getAccountName, user.getAccountName()));
            if (vv > 0) {
                throw new BusinessException("帐号已存在");
            }
        }
        if (ObjectNull.isNotNull(userTenant.getDeptId())) {
            Dept dept = deptMapper.selectById(userTenant.getDeptId());
            userTenant.setDeptName(dept.getName());
        }
        if (ObjectNull.isNotNull(userTenant.getJobId())) {
            Job job = jobMapper.selectById(userTenant.getJobId());
            userTenant.setDeptName(job.getName());
        }
        // 设置初始密码
        TenantPo t = tenantService.getOne(Wrappers.<TenantPo>lambdaQuery().eq(TenantPo::getId, TenantContextHolder.getTenantId()));
        user.setPassword(passwordEncoder.encode(t.getDefaultPassword()));
        // 保存用户
        this.save(user);
        userTenant.setUserId(user.getId());
        userTenantMapper.insert(userTenant);
        return this.getById(user.getId());
    }

    @Override
    public User info(String username) {
        log.debug("用户名密码登录, 用户名: {}, 选择登录租户id: {}", username, TenantContextHolder.getTenantId());
        User user = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccountName, username));
        if (ObjectUtil.isEmpty(user)) {
            //TODO 使用手机号和邮箱匹配 暂时可支持手机号匹配，默认密码123456
            user = getOne(Wrappers.lambdaQuery(new User().setPhone(username)));
            if (ObjectUtil.isEmpty(user)) {
                throw new BusinessException("用户不存在或密码错误");
            }
        }
        // 是否锁定
        if (user.getCancelFlag()) {
            // 所有用户都被禁用时
            throw new BusinessException("用户已被注销, 请联系管理员");
        }
        return user;
    }

    @Override
    public List<User> getByRoleIds(List<String> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            // 角色id集合为空
            return Collections.emptyList();
        }
        StringBuilder builder = new StringBuilder("(");
        for (String roleId : roleIds) {
            if (StringUtils.isBlank(roleId)) {
                // 角色id为null
                return Collections.emptyList();
            }
            builder.append("'" + roleId + "'").append(",");
        }
        // 删除末尾的逗号
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        List<User> userList = userMapper.getByRoleIds(builder.toString());
        if (userList.isEmpty()) {
            return Collections.emptyList();
        }
        return userList;
    }

    @Override
    public List<User> getQueryDeptName(String deptName) {
        return userMapper.getQueryDeptName(deptName);
    }

    @Override
    public User phone(String phone) {
        //找到用户名和密码都匹配的
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
        if (ObjectUtil.isEmpty(user)) {
            throw new BusinessException("用户不存在");
        }
        if (user.getCancelFlag()) {
            throw new BusinessException("用户已被注销, 请联系管理员");
        }
        return user;
    }

    @Override
    public <T> boolean updateInfo(SFunction<User, T> getter, T value, String userId) {
        if (Objects.isNull(getter) || StringUtils.isBlank(userId)) {
            return false;
        }
        return this.update(Wrappers.<User>lambdaUpdate().set(getter, value).eq(User::getId, userId));
    }

    @Override
    public void downloadExcelTemplate(HttpServletResponse response) {
        // 响应数据
        String fileName = "导入用户" + FILE_TYPE;
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(URLUtil.encode(fileName, StandardCharsets.UTF_8)));
        response.setStatus(HttpStatus.OK.value());
        try {
            //导出数据
            new ExcelWriterBuilder()
                    .file(response.getOutputStream())
                    .head(UserExcelTemplate.class)
                    .registerConverter(arrayListConvert)
                    .registerConverter(localDateTimeConvert)
                    .sheet(0)
                    .doWrite(Collections.emptyList());
        } catch (Exception ex) {
            log.error("模板下载异常", ex);
        }
    }

    @Override
    public void importUserExcel(UserDto userDto, MultipartFile file) {
        try {
            // 获取所有部门
            List<SysDeptDto> deptTree = deptServiceApi.getAllTree().getData();
            // 获取所有岗位
            List<SysJobDto> sysJobDtos = authJobServiceApi.getAll().getData();
            // 导入数据
            EasyExcel.read(file.getInputStream(), UserExcelTemplate.class, new ImportUserListener(userDto, deptTree, sysJobDtos)).sheet().doRead();
        } catch (Exception ex) {
            log.error("数据导入异常", ex);
            throw new BusinessException("导入失败: " + ex.getMessage());
        }
    }

    @Override
    public void pull(SyncUserDto syncUser) {
        if (ObjectNull.isNull(syncUser)) {
            return;
        }
        // 排除已存在的用户
        excludeExistUser(syncUser);
        // 分批同步用户数据（只新增）
        if (CollectionUtils.isNotEmpty(syncUser.getUsers())) {
            int i = 0;
            int size = syncUser.getUsers().size();
            int batchSize = SYNC_BATCH_SIZE;
            // 设置初始密码
            TenantPo t = tenantService.getOne(Wrappers.<TenantPo>lambdaQuery().eq(TenantPo::getId, TenantContextHolder.getTenantId()));
            String defaultPassword = passwordEncoder.encode(t.getDefaultPassword());
            while (i < size) {
                long limit = Math.min(i + batchSize, size) - i;
                List<User> currentBatchUsers = syncUser.getUsers().stream().skip(i).limit(limit)
                        .map(user -> user.setPassword(defaultPassword))
                        .collect(Collectors.toList());
                List<UserExtension> currentBatchUserExtensions = syncUser.getUserExtensions().stream().skip(i).limit(limit).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(currentBatchUsers)) {
                    saveBatch(currentBatchUsers);
                }
                if (CollectionUtils.isNotEmpty(currentBatchUserExtensions)) {
                    userExtensionService.saveBatch(currentBatchUserExtensions);
                }
                i += batchSize;
            }
        }
        // 分批同步用户租户数据（新增或修改）
        if (CollectionUtils.isNotEmpty(syncUser.getUserTenants())) {
            int i = 0;
            int size = syncUser.getUserTenants().size();
            int batchSize = SYNC_BATCH_SIZE;
            while (i < size) {
                long limit = Math.min(i + batchSize, size) - i;
                List<UserTenant> currentBatchUserTenants = syncUser.getUserTenants().stream().skip(i).limit(limit).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(currentBatchUserTenants)) {
                    userTenantService.saveOrUpdateBatch(currentBatchUserTenants);
                }
                i += batchSize;
            }
        }

    }

    /**
     *  排除已存在的用户
     *
     * @param syncUser
     */
    private void excludeExistUser(SyncUserDto syncUser) {
        if (CollectionUtils.isEmpty(syncUser.getUsers())) {
            return;
        }
        // 三方平台可能允许一个用户在多个部门中。所以用户信息可能会重复。对用户信息去重
        List<User> users = syncUser.getUsers().stream().filter(distinctByKey(User::getId)).collect(Collectors.toList());
        List<UserTenant> userTenants = syncUser.getUserTenants().stream().filter(distinctByKey(UserTenant::getUserId)).collect(Collectors.toList());
        List<UserExtension> userExtensions= syncUser.getUserExtensions().stream().filter(distinctByKey(UserExtension::getUserId)).collect(Collectors.toList());

        // 已存在的用户id
        List<String> deptUserIds = users.stream().map(User::getId).collect(Collectors.toList());
        int i = 0;
        int size = deptUserIds.size();
        int batchSize = SYNC_BATCH_SIZE;
        while (i < size) {
            List<String> batchUserIds = deptUserIds.stream().skip(i).limit(Math.min(i + batchSize, size) - i).collect(Collectors.toList());
            // 已存在的用户不重新同步
            List<String> existUserIds = Optional.ofNullable(list(Wrappers.<User>lambdaQuery().in(User::getId, batchUserIds))).orElse(new ArrayList<>()).stream().map(User::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(existUserIds)) {
                users.removeIf(user -> existUserIds.contains(user.getId()));
            }
            // 修改已存在的用户租户信息的部门id和部门名称
            List<UserTenant> existTenantUsers = userTenantService.list(Wrappers.<UserTenant>lambdaQuery().in(UserTenant::getUserId, batchUserIds));
            if (CollectionUtils.isNotEmpty(existTenantUsers)) {
                existTenantUsers.forEach(existUserTenant ->
                    userTenants.stream().filter(userTenant -> userTenant.getUserId().equals(existUserTenant.getUserId())).findFirst()
                            .ifPresent(u -> existUserTenant.setDeptId(u.getDeptId()).setDeptName(u.getDeptName()))
                );
                List<String> existTenantUserIds = existTenantUsers.stream().map(UserTenant::getUserId).collect(Collectors.toList());
                userTenants.removeIf(user -> existTenantUserIds.contains(user.getUserId()));
                userTenants.addAll(existTenantUsers);
            }
            // 已存在的用户扩展信息不重新同步
            List<String> existExtensionUserIds = Optional.ofNullable(userExtensionService.list(Wrappers.<UserExtension>lambdaQuery().in(UserExtension::getOpenId, batchUserIds))).orElse(new ArrayList<>()).stream().map(UserExtension::getOpenId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(existExtensionUserIds)) {
                userExtensions.removeIf(user -> existExtensionUserIds.contains(user.getOpenId()));
            }
            i += batchSize;
        }
        syncUser.setUsers(users);
        syncUser.setUserTenants(userTenants);
        syncUser.setUserExtensions(userExtensions);
    }

    /**
     * 去重
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
