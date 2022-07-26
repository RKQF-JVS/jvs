package cn.bctools.common.utils.function;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义获取属性名方法
 *
 * @author guojing
 * @describe 类的属性操作
 */
public class Get {

    static Map<TypeFunction, String> map = new HashMap<>();

    /**
     * 获取对象属性名,使用方法为    Get.name(R::getCode)     =  "code"
     * 这样的编写方式可以不用每次都写固定参数名的值
     *
     * @param name 操作类型
     * @param <T>  入参对象Class
     * @param <R>  返回的结果字段串
     * @return
     */
    public static <T, R> String name(TypeFunction<T, R> name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        String lambdaColumnName = TypeFunction.getLambdaColumnName(name);
        map.put(name, lambdaColumnName);
        return lambdaColumnName;
    }

}
