package cn.bctools.common.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前线程的存储工具，
 * TransmittableThreadLocal 是Alibaba开源的、用于解决 “在使用线程池等会缓存线程的组件情况下传递ThreadLocal”的扩展，比如：线程本地变量在线程池之间的传递问题。若希望 TransmittableThreadLocal 在线程池与主线程间传递，需配合 TtlRunnable 和 TtlCallable 使用。
 * 开源项目地址  {@link http://github.com/alibaba/transmittable-thread-local}
 *
 * @author guojing
 * @describe 统一数据保存
 */
@Slf4j
public class SystemThreadLocal<T> {

    private static ThreadLocal<Map<String, Object>> local = new TransmittableThreadLocal<Map<String, Object>>();

    public static <T> void set(String k, T v) {
        Map<String, Object> map = local.get();
        if (map == null) {
            map = new HashMap<>(80);
            local.set(map);
        }
        local.get().put(k, v);
    }

    public static <T> T get(String key) {
        Map<String, Object> stringObjectMap = local.get();
        if (stringObjectMap == null) {
            HashMap<String, Object> map = new HashMap<>(80);
            local.set(map);
        }
        return (T) local.get().get(key);
    }

    public static void remove(String key) {
        local.get().remove(key);
    }

    /**
     * 清除线程所有的数据
     */
    public static void clear() {
        local.remove();
    }


}
