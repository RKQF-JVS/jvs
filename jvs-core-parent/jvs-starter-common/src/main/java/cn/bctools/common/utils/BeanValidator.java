package cn.bctools.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import cn.bctools.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guojing
 * 参数校验类（JSR 303） 可以直接对其参数完整性进行校验，保证参数无问题 建议使用 {@linkplain Validated} 注解进行处理，并可以对参数进行分组，目前多个项目都在使用此方法,可以在Controller方法上面添加，也可以自己手动处理添加
 */
@Slf4j
public class BeanValidator {

    /**
     * 此注解暂时只支持List泛型，不支持其它泛型
     * 在需要的字段中添加此注解即可将泛型打开，校验内部对象
     *
     * @author guojing
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Genericity {

    }

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static Validator getValidator() {
        return validator;
    }

    /**
     * 是否向下递归处理
     */
    static Map<Class, List<Field>> classVa = new HashMap<>(1);


    /**
     * 校验对象是否满足注解包校验要求；包地址为：javax.validation.constraints
     * 针对所有统一参数对象进行属性校验，如果不通过，则直接将错误信息包装为${@link javax.swing.text.html.Option<List<String>}
     * String 为每一条的错误信息
     *
     * @param object 校验对象
     * @param groups 分组的Class
     * @return Optional   包装处理对象
     */
    public static <T> Optional<List<String>> validator(T object, Class<?>... groups) {
        List<String> arrayList = new ArrayList<>();
        if (null == object) {
            arrayList.add("The object to be validated must not be null.");
            return Optional.ofNullable(arrayList);
        }
        List<String> violations = new ArrayList<>();
        if (classVa.containsKey(object.getClass())) {
            //获取下级所有的错误信息
            //递归拿下级
            List<String> list = getSon(classVa.get(object.getClass()), object);
            violations.addAll(list);
        } else {
            List<Field> collect = Arrays.stream(object.getClass().getDeclaredFields()).filter(e -> e.isAnnotationPresent(Genericity.class)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(collect)) {
                classVa.put(object.getClass(), collect);
            }
            //递归拿下级
            List<String> list = getSon(collect, object);
            violations.addAll(list);
        }
        List<String> list = validator.validate(object, groups).stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        violations.addAll(list);
        if (violations.size() > 0) {
            log.error(JSONObject.toJSONString(violations));
            return Optional.ofNullable(violations);
        } else {
            return Optional.ofNullable(null);
        }
    }

    /**
     * 递归拿下级校验结果
     *
     * @param collect
     * @param object
     * @param <T>
     * @return
     */
    private static <T> List<String> getSon(List<Field> collect, T object) {
        return (List<String>) collect.stream().map(e -> {
            try {
                return e.get(object);
            } catch (IllegalAccessException illegalAccessException) {
                //如果为空值，上一层会自动判断，所以不存在此情况 可不处理
                return null;
            }
        }).filter(ObjectUtil::isNotEmpty)
                .flatMap(e -> {
                    if (e instanceof List) {
                        return ((List) e).stream().flatMap(v -> validator(v)
                                .orElseGet(() -> Collections.emptyList()).stream());
                    } else if (e instanceof Map) {
                        //map不处理
                        return null;
                    } else {
                        return null;
                    }
                })
                //多做一次，过滤空
                .filter(ObjectUtil::isNotEmpty)
                .collect(Collectors.toList());

    }

    /**
     * 校验对象是否满足注解包校验要求；包地址为：javax.validation.constraints
     * 针对所有统一参数对象进行属性校验，如果不通过则直接会自定义异常${@link  BusinessException} 异常信息为注解中的错误提示信息
     * 注：此方法不是${@link org.springframework.stereotype.Controller}上面的方法实现，它是由框架自行处理，此方法只做自定义操作
     *
     * @param object 校验对象
     * @param groups 分组的Class
     * @return
     */
    public static <T> void validatorException(T object, Class<?>... groups) {
        List<String> arrayList = new ArrayList<>();
        if (null == object) {
            arrayList.add("The object to be validated must not be null.");
            throw new BusinessException("数据对象为空");
        }
        List<String> violations = new ArrayList<>();
        if (classVa.containsKey(object.getClass())) {
            //获取下级所有的错误信息
            //递归拿下级
            List<String> list = getSon(classVa.get(object.getClass()), object);
            violations.addAll(list);
        } else {
            List<Field> collect = Arrays.stream(object.getClass().getDeclaredFields()).filter(e -> e.isAnnotationPresent(Genericity.class)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(collect)) {
                classVa.put(object.getClass(), collect);
            }
            //递归拿下级
            List<String> list = getSon(collect, object);
            violations.addAll(list);
        }
        List<String> list = validator.validate(object, groups).stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        violations.addAll(list);
        if (violations.size() > 0) {
            log.error(JSONObject.toJSONString(violations));
            throw new BusinessException(JSONObject.toJSONString(violations));
        }
    }

}
