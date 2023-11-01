package cn.bctools.web.config;

import cn.hutool.core.util.ObjectUtil;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.utils.SystemThreadLocal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通用请求过滤器
 * <p>
 * 处理请求版本, 租户id相关的上下文数据
 *
 * @Author: gj
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ContextHolderFilter extends GenericFilterBean {

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String version = request.getHeader(SysConstant.VERSION);
        if (ObjectUtil.isNotEmpty(version)) {
            SystemThreadLocal.set(SysConstant.VERSION, version);
        }
        String tenantId = request.getHeader(SysConstant.TENANTID);
        if (ObjectUtil.isNotEmpty(tenantId)) {
            SystemThreadLocal.set(SysConstant.TENANTID, tenantId);
        }
        filterChain.doFilter(request, response);
        SystemThreadLocal.clear();
    }

}
