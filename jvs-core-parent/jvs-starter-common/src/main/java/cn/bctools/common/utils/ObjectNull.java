package cn.bctools.common.utils;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多种方式判断是否为空，可以判断集合，对象，空串等
 *
 * @author guojing
 * @describe
 */
public class ObjectNull {

    /**
     * 判断是否存在空对象 可以判断集合
     * 如果存在一个为空的对象，即返回true
     *
     * @param objects 判断对象
     * @return 判断结果
     */
    public static boolean isNull(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (Object e : objects) {
            if (ObjectUtil.isNull(e) || "".equals(e)) {
                return true;
            }
            if (e instanceof Collection) {
                Collection v = (Collection) e;
                if (v.size() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否不包含空对象，如果全部都不为空，则返回true|反之，任何一个为空，都返回false
     *
     * @param objects 判断对象
     * @return 判断结果
     */
    public static boolean isNotNull(Object... objects) {
        return !isNull(objects);
    }

    /**
     * 只要存在一个不为空的数据，即返回true
     *
     * @param objects 判断对象
     * @return 判断结果
     */
    public static boolean isNotNullOne(Object... objects) {
        return Arrays.stream(objects).anyMatch(e -> ObjectUtil.isNotNull(e) && !"".equals(e));
    }

    /**
     * 过滤所有不为空的数据
     *
     * @param objects 判断对象
     * @return 判断结果
     */
    public static List filterNull(Object... objects) {
        return Arrays.stream(objects).filter(e -> ObjectUtil.isNotNull(e) && !"".equals(e)).collect(Collectors.toList());
    }
}
