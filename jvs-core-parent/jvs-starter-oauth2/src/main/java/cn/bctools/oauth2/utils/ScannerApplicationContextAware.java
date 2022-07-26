package cn.bctools.oauth2.utils;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.common.entity.dto.DataDictDto;
import cn.bctools.common.entity.dto.RequestUrlDto;
import cn.bctools.common.entity.dto.ScannerDto;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.annotation.JvsDataTable;
import cn.bctools.database.annotation.JvsDataTableField;
import cn.bctools.database.interfaces.DataEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动扫描器， 扫描所有的请求资源路径，然后请路径发送到网关，存到redis中。需要通过加密凭证， 和key，才能发送，否则发送失败
 *
 * @author Administrator
 */
@Slf4j
@Order(100000)
@Configuration
@ConditionalOnMissingBean(ScannerApplicationContextAware.class)
public class ScannerApplicationContextAware implements ApplicationContextAware {

    @Resource
    OAuth2ClientProperties oAuth2ClientProperties;

    @Resource
    RestTemplate restTemplate;

    @Value("${gatewayUrl}")
    String gatewayUrl;

    /**
     * 〈刷新服务的所有URI〉
     *
     * @since: 1.0.0
     */
    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.debug("权限扫描器取全部url地址对象");
        final String applicationContextName = applicationContext.getEnvironment().getProperty("spring.application.name");
        final List<RequestUrlDto> list = getRequestMapping(applicationContext);

        setApplicationPrefix(list, applicationContextName);

        ScannerDto scannerDto = BeanCopyUtil.copy(oAuth2ClientProperties, ScannerDto.class).setList(list);
        scannerDto.setApplicationName(applicationContextName);
        log.debug("请求消息扫描器地址: {}", JSONObject.toJSONString(scannerDto));
        //扫描数据权限
        List<DataDictDto> dataScopeTable = getDataScopeTable();

        if (dataScopeTable.isEmpty()) {
            log.info("没有数据权限支持");
            scannerDto.setDataDictDtos(Collections.emptyList());
        } else {
            scannerDto.setDataDictDtos(dataScopeTable);
        }
        String decodedPassword = PasswordUtil.encodePassword(JSONObject.toJSONString(scannerDto), SpringContextUtil.getKey());

        // 项目启动时直接将初始化数据注册至网关
        String url = gatewayUrl + "/gateway/handler/mapping";
        log.info("请求网关: {}", url);
        try {
            restTemplate.postForObject(url, decodedPassword, R.class);
            //如果没有权限扫描，则直接失败，退出系统
        } catch (Exception e) {
            log.warn("[{}] 请添加requestMappingHandlerMapping", SpringContextUtil.getApplicationContextName());
        }

    }

    /**
     * 扫描当前哪些系统有哪些数据权限字段
     *
     * @return
     */
    public static List<DataDictDto> getDataScopeTable() {
        final Class<JvsDataTable> table = JvsDataTable.class;
        final Class<JvsDataTableField> fields = JvsDataTableField.class;
        List<DataDictDto> list = new ArrayList<>();
        // 获取带注解的数据库映射(PO)类
        Set<Class<?>> poClasses = ClassUtil.scanPackageByAnnotation("cn.bctools", table);
        for (Class<?> po : poClasses) {
            JvsDataTable tableAnnotation = po.getAnnotation(table);
            DataDictDto dataDictDto = new DataDictDto()
                    .setValue(tableAnnotation.value())
                    .setDesc(tableAnnotation.desc())
                    .setDataDictDto(new ArrayList<>());
            Arrays.stream(po.getDeclaredFields())
                    .filter(v -> v.isAnnotationPresent(fields))
                    .forEach(s -> {
                        // 获取字段属性注解
                        JvsDataTableField fieldAnnotation = s.getAnnotation(fields);
                        // 获取该枚举类的各个值
                        List<DataDictDto> collect = Arrays.stream((DataEnum[]) s.getType().getEnumConstants())
                                .map(a -> new DataDictDto().setValue(a.toString()).setDesc(a.getDesc()))
                                .collect(Collectors.toList());
                        DataDictDto fieldDto = new DataDictDto()
                                .setValue(fieldAnnotation.name())
                                .setDesc(fieldAnnotation.desc())
                                .setDataDictDto(collect);
                        dataDictDto.getDataDictDto().add(fieldDto);
                    });
            // 如果下级为空，则表示全部不开启
            if (ObjectUtils.isNotEmpty(dataDictDto.getDataDictDto())) {
                list.add(dataDictDto);
            }
        }
        return list;
    }

    /**
     * 获取系统的扩展名，如果为mgr 则直接拼凑为/mgr  反之拼凑为/api
     *
     * @param uris            扫描的数据
     * @param applicationName 系统名称
     * @return
     */
    private static void setApplicationPrefix(List<RequestUrlDto> uris, String applicationName) {
        String[] split = applicationName.split(StrUtil.DASHED);
        String anotherString = split[split.length - 1];
        String prefix = "mgr".equalsIgnoreCase(anotherString) ? "mgr" : "api";
        applicationName = "/" + prefix + "/" + applicationName.replaceAll("-mgr", "").replaceAll("-biz", "") + "/";
        for (RequestUrlDto e : uris) {
            //组装请求头与上级的地址
            e.setApi((applicationName + e.getApi()).replaceAll("//", "/"));
        }
    }

    /**
     * 扫描请求地址
     *
     * @param applicationContext
     * @return
     */
    public static List<RequestUrlDto> getRequestMapping(ApplicationContext applicationContext) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = applicationContext.getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods();
        List<RequestUrlDto> list = new ArrayList<>();
        for (RequestMappingInfo info : handlerMethods.keySet()) {
            String uri = info.getPatternsCondition().getPatterns().iterator().next();
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            if (methods.isEmpty()) {
                // FIXME 使用@RequestMapping的接口, methods会为空
                continue;
            }
            RequestMethod firstMethod = methods.iterator().next();
            // TODO 接口注解
            list.add(new RequestUrlDto().setApi(uri).setMethod(firstMethod));
        }
        return list;
    }

}
