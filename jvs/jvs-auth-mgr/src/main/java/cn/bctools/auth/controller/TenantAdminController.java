package cn.bctools.auth.controller;

import cn.bctools.auth.entity.LoginLog;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.service.*;
import cn.bctools.auth.service.impl.SysConfigServiceImpl;
import cn.bctools.auth.vo.SpaceVo;
import cn.bctools.auth.vo.TenantSpaceVo;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.log.annotation.Log;
import cn.bctools.message.push.api.AliSmsApi;
import cn.bctools.message.push.api.WechatOfficialAccountApi;
import cn.bctools.message.push.dto.vo.AliSmsTemplateVo;
import cn.bctools.message.push.dto.vo.WxMpTemplateVo;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.service.FileDataInterface;
import cn.bctools.oss.template.OssTemplate;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 
 */
@Slf4j
@Api(tags = "租户管理员")
@RestController
@AllArgsConstructor
@RequestMapping("/tenant/admin/base")
public class TenantAdminController {

    UserTenantService userTenantService;
    UserService userService;
    DeptService deptService;
    UserGroupService userGroupService;
    LoginLogService loginLogService;
    FileDataInterface fileDataInterface;
    OssTemplate ossTemplate;
    RedisUtils redisUtils;
    WechatOfficialAccountApi wechatOfficialAccountApi;
    AliSmsApi aliSmsApi;
    SysConfigServiceImpl sysConfigService;

    TenantService tenantService;

    @SneakyThrows
    @ApiOperation(value = "基础信息")
    @GetMapping
    public R base() {
        String today = DateUtil.today();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse(today);
        //少一天
        Date st = new Date(parse.getTime() - 86400000);
        int count = loginLogService.list(new LambdaQueryWrapper<LoginLog>().select(LoginLog::getUserId).between(LoginLog::getOperateTime, st, parse).groupBy(LoginLog::getUserId)).size();
        Dict set = Dict.create().set("tenant", UserCurrentUtils.getCurrentUser().getTenant())
                .set("users", userTenantService.count())
                .set("depts", deptService.count())
                .set("groups", userGroupService.count())
                .set("yesterdays", count);
        return R.ok(set);
    }

    @ApiOperation(value = "公众号消息模板配置", notes = "获取当前租户的公众号消息模板配置信息接口")
    @GetMapping("/WECHAT_MP/message")
    public R weChatMpMessage(@RequestParam(value = "new", defaultValue = "false") Boolean niu) {
        try {
            String key = "message:push:WECHAT_MP_MESSAGE" + TenantContextHolder.getTenantId();
            ;
            //如果是不是最新的，则直接返回缓存数据
            if (redisUtils.hasKey(key) && !niu) {
                return R.ok(redisUtils.get(key));
            } else {
                List<WxMpTemplateVo> allPrivateTemplate = wechatOfficialAccountApi.getAllPrivateTemplate();
                redisUtils.set(key, allPrivateTemplate);
                return R.ok(allPrivateTemplate);
            }
        } catch (Exception e) {
            log.error("消息服务异常", e);
        }
        return R.ok();
    }

    @ApiOperation(value = "短信消息模板配置", notes = "获取当前租户的公众号消息模板配置信息接口")
    @GetMapping("/SMS/login/messageCode")
    public R setLoginMessageCode(@RequestParam("templateCode") String templateCode) {
        String key = "message:push:SMS" + TenantContextHolder.getTenantId();
        //设置
        if (redisUtils.hasKey(key)) {
            Map<String, Object> config = sysConfigService.getConfig(SysConstant.FRAME, SysConfigTypeEnum.SMS);
            config.put("templateCode", templateCode);
            //更新数据
            sysConfigService.saveConfig(TenantContextHolder.getTenantId(), SysConstant.FRAME, SysConfigTypeEnum.SMS, config);
        }
        return R.ok();
    }

    @ApiOperation(value = "短信消息模板配置", notes = "获取当前租户的公众号消息模板配置信息接口")
    @GetMapping("/SMS/message")
    public R smsMessage(@RequestParam(value = "new", defaultValue = "false") Boolean niu, @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        try {
            String key = "message:push:SMS" + TenantContextHolder.getTenantId();
            //如果是不是最新的，则直接返回缓存数据
            List<AliSmsTemplateVo> allPrivateTemplate = new ArrayList<>();
            if (redisUtils.hasKey(key) && !niu) {
                allPrivateTemplate = (List<AliSmsTemplateVo>) redisUtils.get(key);
            } else {
                allPrivateTemplate = aliSmsApi.getAllPrivateTemplate(pageIndex, pageSize);
            }
            Map<String, Object> config = sysConfigService.getConfig(SysConstant.FRAME, SysConfigTypeEnum.SMS);
            if (config.containsKey("templateCode")) {
                Object templateCode = config.get("templateCode");
                allPrivateTemplate.forEach(e -> {
                    if (e.getTemplateCode().equals(templateCode)) {
                        e.setLoginCode(true);
                    } else {
                        e.setLoginCode(false);
                    }
                });
            }
            //获取缓存配置
            redisUtils.set(key, allPrivateTemplate);
            return R.ok(allPrivateTemplate);
        } catch (Exception e) {
            log.error("消息服务异常", e);
        }
        return R.ok();
    }

    @GetMapping("/config/{type}")
    @ApiOperation(value = "获取租户应用配置")
    public R getTenantApp(@PathVariable SysConfigTypeEnum type) {
        String tenantId = TenantContextHolder.getTenantId();
        String clientId = UserCurrentUtils.getCurrentUser().getClientId();
        //如果是超级管理员，需要校验，如果不是，只能是自己修改自己的
        Map<String, Object> key = sysConfigService.key(tenantId, clientId, type);
        return R.ok(key);
    }

    @GetMapping("/check")
    @ApiOperation(value = "检查配置是否配置了如果有配置则可以使用，未配置，则不能使用")
    public R<List<SysConfigTypeEnum>> check() {
        String tenantId = TenantContextHolder.getTenantId();
        String clientId = UserCurrentUtils.getCurrentUser().getClientId();
        //如果是超级管理员，需要校验，如果不是，只能是自己修改自己的
        List<SysConfigTypeEnum> collect = Arrays.stream(SysConfigTypeEnum.values())
                .filter(e -> {
                    //判断是否有启动这个配置
                    Map<String, Object> key = sysConfigService.key(tenantId, clientId, e);
                    if (ObjectNull.isNull(key)) {
                        return false;
                    } else {
                        return true;
                    }
                }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @GetMapping("/space")
    @ApiOperation(value = "当前租户空间")
    public R space() {
        //获取当前租户的空间存储数据
        String tenantId = UserCurrentUtils.getCurrentUser().getTenantId();
        String key = SysConstant.redisKey("size", "tenant" + tenantId);
        if (redisUtils.hasKey(key)) {
            Object o = redisUtils.get(key);
            //发起统计
            getData(key, tenantId);
            return R.ok(JSONObject.parse(o.toString()));
        }
        return R.ok(getData(key, key));
    }

    @Async
    public TenantSpaceVo getData(String key, String tenantId) {
        TenantContextHolder.setTenantId(tenantId);
        TenantSpaceVo tenantSpaceVo = new TenantSpaceVo();
        List<SpaceVo> objects = new ArrayList<>();
        Integer fileSumSize = 0;
        Integer dataSumSize = 0;
        Map<String, Long> map = fileDataInterface.fileSize().stream().collect(Collectors.toMap(BaseFile::getBucketName, BaseFile::getSize));
        for (Long value : map.values()) {
            fileSumSize += Integer.valueOf(String.valueOf(value));
        }
        try {
            dataSumSize = 0;
        } catch (Exception e) {
        }
        objects.add(SpaceVo.builder().name("公共文件").size(DataSizeUtil.format(map.getOrDefault("jvs-public", Long.valueOf(0)))).build());
        objects.add(SpaceVo.builder().name("大屏文件").size(DataSizeUtil.format(map.getOrDefault("jvs-screen", Long.valueOf(0)))).build());
        objects.add(SpaceVo.builder().name("表单文件").size(DataSizeUtil.format(map.getOrDefault("jvs-form-design", Long.valueOf(0)))).build());
//        objects.add(SpaceVo.builder().name("表单文件").size(DataSizeUtil.format(map.getOrDefault("jvs-form-design", Long.valueOf(0)))).build());
//        objects.add(SpaceVo.builder().name("无忧*文档文件").size("14GB").build());
//        objects.add(SpaceVo.builder().name("无忧*任务文件").size("15GB").build());
//        objects.add(SpaceVo.builder().name("无忧*管理文件").size("16GB").build());
        tenantSpaceVo.setFileSumSize(DataSizeUtil.format(fileSumSize) + "/(不限制)");
        tenantSpaceVo.setDataSumSize(DataSizeUtil.format(dataSumSize) + "/(不限制)");
        tenantSpaceVo.setList(objects);
        redisUtils.set(key, JSONObject.toJSONString(tenantSpaceVo));
        return tenantSpaceVo;
    }

    @Log
    @ApiOperation(value = "注销租户", notes = "删除组织，将导致所有信息无法访问")
    @DeleteMapping
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> delete() {
        String id;
        if (UserCurrentUtils.getCurrentUser().getAdminFlag()) {
            id = UserCurrentUtils.getCurrentUser().getTenantId();
        } else {
            return R.failed("管理员才能注销组织");
        }
        String userId = UserCurrentUtils.getUserId();

        TenantPo byId = tenantService.getById(id);
        //平台帐号
        if (ObjectNull.isNull(byId.getParentId())) {
            throw new BusinessException("不允许解散平台租户");
        }
        // 只能由管理员删除
        boolean removed = tenantService.remove(Wrappers.<TenantPo>lambdaQuery()
                .eq(TenantPo::getId, id)
                .eq(TenantPo::getAdminUserId, userId));
        if (!removed) {
            return R.failed("组织不存在或没有操作权限");
        }
        //删除组织下所有的用户
        //删除所有的用户
        Set<String> list = userTenantService.list(new LambdaQueryWrapper<UserTenant>().select(UserTenant::getUserId)).stream().map(UserTenant::getUserId).collect(Collectors.toSet());
        userTenantService.removeByIds(list);
        //清除租户
        TenantContextHolder.clear();
        //还有的
        Set<String> userIds = userTenantService.list(new LambdaQueryWrapper<UserTenant>().select(UserTenant::getUserId).in(UserTenant::getUserId, list).groupBy(UserTenant::getUserId)).stream().map(e -> e.getUserId()).collect(Collectors.toSet());
        //删除帐号
        list.removeAll(userIds);
        if (ObjectNull.isNotNull(list)) {
            //删除帐号
            userService.removeByIds(list);
        }
        return R.ok(true, "注销成功");
    }
}
