package cn.bctools.dynamic.filter;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 动态数据源Filter
 *
 * @author auto
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DynamicDataSourceFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        // 清空线程中的数据源切换信息, 不影响后续请求
        DynamicDataSourceContextHolder.clear();
    }

}
