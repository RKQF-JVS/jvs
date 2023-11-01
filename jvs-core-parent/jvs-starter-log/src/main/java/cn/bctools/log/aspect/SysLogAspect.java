package cn.bctools.log.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.utils.*;
import cn.bctools.log.annotation.Log;
import cn.bctools.log.annotation.LogIgnore;
import cn.bctools.log.event.LogApplicationEvent;
import cn.bctools.log.po.LogPo;
import cn.bctools.log.service.LogService;
import cn.bctools.log.utils.IpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


/**
 * 注解日志拦截处理，将所有的拦截日志和业务日志通过注入的Bean保存到数据库中
 *
 * @author My_gj
 */
@Slf4j
@Aspect
@Data
@Configuration
@ConfigurationProperties(prefix = "log.aop")
public class SysLogAspect {

    public static final int INT = 1000;

    @Resource
    LogService logService;

    @SneakyThrows
    @Around("@annotation(logannotation)")
    public Object around(ProceedingJoinPoint point, Log logannotation) {
        LogPo logPo = new LogPo();
        IpUtils.getIpAddr(logPo);
        initParameters(point, logannotation, logPo);
        //获取AOP注解的缓存值,与swagger的aop同用，二选一，aop的值为主
        logPo.setStatus(true);

        //获取当前登录用户
        UserInfoDto o = SystemThreadLocal.get("user");
        try {
            logPo.setUserName(o.getUserDto().getRealName());
            logPo.setThreadUser(o.getUserDto());
            logPo.setClientId(o.getUserDto().getClientId());
            //替换用户的IP地址
            if (ObjectNull.isNotNull(logPo.getIp())) {
                logPo.setIp(o.getUserDto().getIp());
            } else {
                //转换IP地址加服务地址
                String transition = IpUtils.transition(logPo.getIp());
                logPo.setIp(transition);
            }
        } catch (Exception ignored) {
        }
        String tid = TraceContext.traceId();
        tid = ObjectUtil.isEmpty(tid) ? "--" : tid;
        logPo.setBusinessName(SpringContextUtil.getApplicationContextName())
                .setStartTime(LocalDateTime.now())
                .setClassName(point.getTarget().getClass().getName())
                .setMethodName(point.getSignature().getName())
                .setEnv(SpringContextUtil.getEnv())
                .setVersion(SpringContextUtil.getVersion())
                .setTenantId(TenantContextHolder.getTenantId())
                .setCreateDate(LocalDateTime.now())
                .setTid(tid);

        Object obj = null;
        Throwable thr = null;

        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            log.error("AOP拦截到错误", throwable);
            //转一次，如果有异常直接抛
            thr = throwable;
            logPo.setExceptionMessage(thr.getMessage());
            logPo.setElements(StackTraceElementUtils.logThrowableToString(throwable));
            logPo.setStatus(false);
        }

        if (obj instanceof R) {
            R r = (R) obj;
            //设置状态值
            logPo.setStatus(r.is());
        }

        LocalDateTime endTime = LocalDateTime.now();
        /*判断是否记录返回结果*/
        if (logannotation.back()) {
            if (ObjectUtil.isNotEmpty(obj)) {
                logPo.setReturnObj("return:" + JSONObject.toJSONString(obj));
            }
        }
        logPo.setEndTime(endTime);
        logPo.setConsumingTime(Duration.between(logPo.getStartTime(), logPo.getEndTime()).toMillis());
        //如果执行时间超过一秒时间,直接给提示
        if (logPo.getConsumingTime() > INT) {
            String s = Optional.of(logPo).map(LogPo::getFunctionName).orElse("");
            log.warn("请优化接口! The API execution time is too large, please optimize !  {} ms {}  , api : {}", logPo.getConsumingTime(), s, logPo.getApi());
        }
        //保存执行信息
        try {
            SpringContextUtil.getApplicationContext().publishEvent(new LogApplicationEvent(this, logPo));
        } catch (Exception e) {
            log.error("保存请求日志异常", e);
        }

        log.trace("aop拦截日志: {}", JSONObject.toJSONString(logPo));
        //判断是否是本地IP,如果是本地ip ，则不保存
        if (Optional.ofNullable(thr).isPresent()) {
            throw thr;
        }
        return obj;
    }

    /**
     * 获取参数，可能会有忽略字段记录，或无法格式化的对象值
     *
     * @param point 字段说明字段说明
     * @author: guojing
     * @return: java.lang.Object[]
     */
    private void initParameters(ProceedingJoinPoint point, Log loga, LogPo logPo) throws NoSuchFieldException, IllegalAccessException {
        MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = ((MethodInvocationProceedingJoinPoint) point);
        Field field = MethodInvocationProceedingJoinPoint.class.getDeclaredField("methodInvocation");
        field.setAccessible(true);
        ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) field.get(methodInvocationProceedingJoinPoint);
        Method method = proxyMethodInvocation.getMethod();
        Annotation[] annotations = method.getAnnotations();
        //获取操作类型
        String operationType = loga.operationType();
        logPo.setOperationType(operationType);
        if (ObjectNull.isNull(loga.value())) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ApiOperation) {
                    ApiOperation operation = (ApiOperation) annotation;
                    logPo.setFunctionName(operation.value());
                    if (ObjectNull.isNotNull(logPo.getOperationType())) {
                        logPo.setOperationType(operation.value());
                    }
                }
            }
        } else {
            logPo.setFunctionName(loga.value());
        }
        try {
            //判断有没有加API注解,如果有，直接加上前半段数据
            Class<?> aClass = point.getTarget().getClass();
            if (aClass.isAnnotationPresent(Api.class)) {
                logPo.setFunctionName(aClass.getAnnotation(Api.class).tags()[0] + "-" + logPo.getFunctionName());
            }
        } catch (Exception ignored) {

        }
        //设置值
        Object[] objects = new Object[point.getArgs().length];
        for (int i = 0; i < point.getArgs().length; i++) {
            Object arg = point.getArgs()[i];
            //增加参数忽略匹配  如果是返回对象不记录
            if (arg instanceof com.baomidou.mybatisplus.extension.plugins.pagination.Page) {
                //改造排序参数
                Page page = (Page) arg;
                for (Object order : page.getOrders()) {
                    OrderItem orderItem = (OrderItem) order;
                    orderItem.setColumn(StringUtils.camelToUnderline(((OrderItem) order).getColumn()));
                }
                point.getArgs()[i] = page;
                continue;
            }
            //增加参数忽略匹配  如果是返回对象不记录
            if (arg instanceof HttpServletResponse) {
                continue;
            }
            //增加参数忽略匹配  如果是请求Req对象不记录
            if (arg instanceof HttpServletRequest) {
                continue;
            }
            //增加参数忽略匹配  如果是文件上传不记录
            if (arg instanceof MultipartFile) {
                continue;
            }
            //增加参数忽略匹配  如果是文件上传不记录
            if (arg instanceof BindingAwareModelMap) {
                continue;
            }
            //如果有，表示忽略，就需要删除
            if (Arrays.stream(method.getParameterAnnotations()[i]).noneMatch(e -> e instanceof LogIgnore)) {
                //直接存放到某个对象中
                objects[i] = point.getArgs()[i];
            }
        }
        if (objects.length > 0) {
            logPo.setParameters(objects);
        }
        log.info("调用方法:{}", logPo.getFunctionName());
    }

}
