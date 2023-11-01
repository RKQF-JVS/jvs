package cn.bctools.common.utils;

import com.google.common.collect.Lists;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * @author yxh
 * @ClassName: DozerUtil
 * @Description: Dozerg工具类(这里用一句话描述这个类的作用)
 */
public class DozerUtil {

    private DozerUtil() {
    }

    private static DozerBeanMapper instance;

    public static synchronized Mapper getInstance() {
        if (instance == null) {
            instance = new DozerBeanMapper();
        }
        return instance;
    }

    /**
     * 基于Dozer转换对象的类型.
     *
     * @param source           源对象
     * @param destinationClass 目标类信息
     * @param <T>              目标类型
     * @return 返回目标对象
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        return getInstance().map(source, destinationClass);
    }

    /**
     * 基于Dozer将对象A的数组拷贝到对象B数组中.
     *
     * @param sourceList       源数组
     * @param destinationClass 目标对象
     * @param <T>              目标类型
     * @return 返回目标数组
     */
    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        List<T> destinationList = Lists.newArrayList();
        for (Object sourceObject : sourceList) {
            T destinationObject = getInstance().map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 基于Dozer将对象A的值拷贝到对象B中.
     *
     * @param source            源对象
     * @param destinationObject 目标对象
     */
    public static void copy(Object source, Object destinationObject) {
        getInstance().map(source, destinationObject);
    }
}
