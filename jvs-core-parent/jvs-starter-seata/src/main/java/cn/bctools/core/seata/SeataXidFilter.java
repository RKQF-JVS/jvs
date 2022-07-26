package cn.bctools.core.seata;

import io.seata.core.context.RootContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 绑定分布式事务xid
 *
 * @Author: GuoZi
 */
@Slf4j
@Order
@Component
public class SeataXidFilter extends GenericFilterBean {

    public static final String SEATA_XID_KEY = "TX_SEATA_XID";

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestXid = request.getHeader(SEATA_XID_KEY);
        if (StringUtils.isNotBlank(requestXid)) {
            // 绑定Feign接口传递过来的xid
            RootContext.bind(requestXid);
        }
        filterChain.doFilter(request, servletResponse);
        // 清除xid, 否则会影响到后续请求
        RootContext.unbind();
    }

}