package cn.bctools.auth.component;

import cn.bctools.auth.component.cons.EmailCons;
import cn.bctools.auth.component.cons.SaveUserDto;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import cn.bctools.auth.service.SysConfigService;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.email.EmailUtils;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.redis.utils.RedisUtils;
import cn.bctools.sms.config.AliSmsConfig;
import cn.bctools.sms.utils.SmsSendUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jolokia.util.Base64Util;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Administrator
 */
@Slf4j
@Component
@AllArgsConstructor
public class SmsEmailComponent {

    RedisUtils redisUtils;
    AliSmsConfig aliSmsConfig;
    EmailUtils emailUtils;
    SysConfigService sysConfigService;


    public String type() {
        if (ObjectUtil.isNull(aliSmsConfig.getTemplate())) {
            throw new BusinessException("没有配置消息模板");
        }
        return aliSmsConfig.getTemplate().getLogin();
    }

    /**
     * 验证手机号和验证码是否合规
     *
     * @param phone            手机号
     * @param code             验证码
     * @param exceptionHandler 异常
     */
    public void check(String phone, String code, Supplier<BusinessException> exceptionHandler) {
        // 验证Redis是否存在
        boolean exists = redisUtils.exists("sms:" + type() + ":" + phone + ":" + code);
        if (!exists) {
            throw exceptionHandler.get();
        }
    }

    /**
     * 阿里云短信发送
     *
     * @return 阿里云数据返回
     */
    public void sendPhoneCode(String phone) {
        Map<String, String> mappings = new HashMap<>(2);
        String code = SmsSendUtils.smsCode();
        mappings.put("code", code);

        //当前这个租户的配置信息
        Map<String, Object> map = sysConfigService.key(TenantContextHolder.getTenantId(), SysConstant.FRAME, SysConfigTypeEnum.SMS);
        if (map.isEmpty()) {
            throw new BusinessException("平台未开启短信配置");
        }
        if (!map.containsKey("templateCode")) {
            throw new BusinessException("未指定验证码模板");
        }
        //获取短信配置的逻辑
        AliSmsConfig copy = BeanUtil.mapToBean(map, AliSmsConfig.class, false, CopyOptions.create());
        SmsSendUtils.aliImpl(copy, String.valueOf(map.get("templateCode")), Collections.singletonList(phone), mappings);

        //验证Redis是否存在
        String key = "sms:" + type() + ":" + phone + ":" + code;
        redisUtils.setExpire(key, code, 5, TimeUnit.MINUTES);
        log.info("发送短信消息，手机号为:{},验证码为:{}", JSONObject.toJSONString(phone), code);
    }

    /**
     * 给用户手机号，和邮箱发送添加邀请信息
     *
     * @param currentUser
     * @param toUser      用户对象
     * @param tenantPo    租户信息
     */
    @Async
    public void sendUserInvite(UserDto currentUser, User toUser, TenantPo tenantPo) {
        try {

            //直接获取系统租户配置信息
            Map<String, Object> map = sysConfigService.key(TenantContextHolder.getTenantId(), SysConstant.FRAME, SysConfigTypeEnum.EMAIL);
            if (map.isEmpty()) {
                throw new BusinessException("平台未开启邮件配置");
            }
            MailAccount copy = BeanUtil.mapToBean(map, MailAccount.class, false, CopyOptions.create());


            String title = String.format(EmailCons.EMAIL_TILE, toUser.getRealName(), currentUser.getRealName(), tenantPo.getName());
            SaveUserDto saveUserDto = new SaveUserDto()
                    .setUser(toUser.getRealName())
                    .setDate(DateUtil.formatDate(new Date()))
                    .setContent(EmailCons.EMAIL_CONTENT)
                    .setImg(tenantPo.getLogo())
                    .setTenant(tenantPo.getName())
                    .setTitle(tenantPo.getName());
            Map<String, Object> data = BeanUtil.beanToMap(saveUserDto);
            //替换数据
            String html = StrUtil.format(readHtml(), data);
            //替换数据
            //批量发送邮箱
            String email = toUser.getEmail();
            emailUtils.sendEmailMessage(copy, title, html, Collections.singletonList(email), null);
        } catch (Exception e) {
            log.error("邮箱发送异常:", e);
        }
    }

    public String readHtml() {
        ClassPathResource classPathResource = new ClassPathResource("static/email.saveuser.html");
        byte[] bytes = IoUtil.readBytes(classPathResource.getStream());
        String s = new String(bytes);
        return s;
    }

    public void sendEmailCode(UserDto currentUser, String email, TenantPo tenantPo) {
        try {
            //直接获取系统租户配置信息
            Map<String, Object> map = sysConfigService.key(TenantContextHolder.getTenantId(), SysConstant.FRAME, SysConfigTypeEnum.EMAIL);
            if (map.isEmpty()) {
                throw new BusinessException("平台未开启邮件配置");
            }
            MailAccount copy = BeanUtil.mapToBean(map, MailAccount.class, false, CopyOptions.create());

            String idStr = SmsSendUtils.smsCode();
            redisUtils.setExpire("emailcode:" + email, idStr, 5, TimeUnit.MINUTES);
            String title = String.format(EmailCons.EMAIL_BIND_TITLE, idStr);
            SaveUserDto saveUserDto = new SaveUserDto()
                    .setUser(currentUser.getRealName())
                    .setDate(DateUtil.formatDateTime(new Date()))
                    .setContent(String.format(EmailCons.EMAIL_CONTENT, idStr))
                    .setImg(tenantPo.getLogo())
                    .setTenant(tenantPo.getName())
                    .setTitle(tenantPo.getName());
            Map<String, Object> data = BeanUtil.beanToMap(saveUserDto);
            if (ObjectUtil.isNull(tenantPo.getLogo())) {
                String encode = "data:image/png;base64," + Base64Util.encode(HttpUtil.downloadBytes(tenantPo.getLogo()));
                //转 base64
                data.put("img", encode);
            }
            //替换数据
            String html = StrUtil.format(readHtml(), data);
            //替换数据
            emailUtils.sendEmailMessage(copy, title, html, Collections.singletonList(email), null);
        } catch (Exception e) {
            log.error("邮箱发送异常:", e);
        }
    }

    public void checkEmailCode(String email, String code, Supplier<BusinessException> supplier) {
        //验证Redis是否存在
        Object exists = redisUtils.get("emailcode:" + email);
        if (ObjectUtil.isEmpty(exists) || !exists.equals(code)) {
            throw supplier.get();
        }
    }
}
