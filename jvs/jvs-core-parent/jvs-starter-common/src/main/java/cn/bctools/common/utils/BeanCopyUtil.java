package cn.bctools.common.utils;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @describe
 */
@Slf4j
public class BeanCopyUtil {

    /**
     * @describe 把数据集转换为另一种数据集
     * @author 
     * @returnType
     */
    public static <T> List<T> copys(List source, Class<T> cls) {
        List<T> arrayList = new ArrayList();
        for (Object t : source) {
            try {
                Object o = cls.newInstance();
                BeanUtil.copyProperties(t, o);
                arrayList.add((T) o);
            } catch (InstantiationException e) {
                log.info("转化错误", e);
            } catch (IllegalAccessException e) {
                log.info("转化错误", e);
            }
        }
        return arrayList;
    }

    /**
     * @param source 源对象
     * @param cls    目标对象
     * @param <T>
     * @return
     */
    public static <T> T copy(Object source, Class<T> cls) {
        T o = null;
        try {
            if (source instanceof List) {
                return (T) copys((List) source, cls);
            } else {
                o = cls.newInstance();
                BeanUtil.copyProperties(source, o);
                return o;
            }
        } catch (InstantiationException e) {
            log.info("转化错误", e);
        } catch (IllegalAccessException e) {
            log.info("转化错误", e);
        }
        return o;
    }

    /**
     * @param cls    目标对象
     * @param source 每一个对象，按顺序copy
     * @param <T>
     * @return
     */
    public static <T> T copy(Class<T> cls, Object... source) {
        T o = null;
        try {
            o = cls.newInstance();
            for (Object o1 : source) {
                BeanUtil.copyProperties(o1, o);
            }
            return o;
        } catch (InstantiationException e) {
            log.info("转化错误", e);
        } catch (IllegalAccessException e) {
            log.info("转化错误", e);
        }
        return o;
    }

    /**
     * @param source 源对象
     * @param target 目标对象
     * @return
     */
    public static Object copy(Object source, Object target) {
        BeanUtil.copyProperties(source, target);
        return target;
    }

}
