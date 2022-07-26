package cn.bctools.database.interceptor.other;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预留其它自定义插件扩展
 *
 * @author guojing
 */
public interface CustomOthersInterceptor {

    /**
     * 获取自定义插件扩展，多个
     *
     * @return
     */
    List<InnerInterceptor> gets();

    default List<InnerInterceptor> init(InnerInterceptor... innerInterceptors) {
        return Arrays.stream(innerInterceptors).collect(Collectors.toList());
    }

}
