package cn.bctools.oauth2.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.*;
import cn.bctools.database.interceptor.datascope.DataScopeContextHolder;
import cn.bctools.oauth2.dto.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理用户信息
 *
 * @Author: GuoZi
 */
@Slf4j
public class JvsUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    /**
     * 后台服务后缀
     */
    public static final String MGR = "-mgr";
    /**
     * api服务后缀
     */
    public static final String BIZ = "-biz";

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        CustomUser customUser = BeanUtil.mapToBean(map, CustomUser.class, false, CopyOptions.create());
        UserInfoDto<UserDto> user = BeanCopyUtil.copy(customUser, UserInfoDto.class);
        if (Objects.isNull(user.getUserDto())) {
            // Token解析异常
            throw new BusinessException("授权无效或已过期");
        }
        // 1. 缓存用户信息
        SystemThreadLocal.set("user", user);
        // 2. 缓存租户id
        TenantContextHolder.setTenantId(user.getUserDto().getTenantId());
        // 3. 缓存数据权限 (对于任意一个请求, 应当只对应一个数据权限)
        List<DataScopeDto> dataScopeList = user.getDataScope();
        DataScopeContextHolder.setDataScope(this.getMatchedDataScope(dataScopeList));
        return new UsernamePasswordAuthenticationToken(user, "N/A", customUser.getAuthorities());
    }

    /**
     * 根据请求路径获取最匹配的数据权限信息
     * <p>
     * 例: 请求路径为 /a/b/c
     * 匹配路径的优先级: /a/b/* > /a/b/** > /a/**
     *
     * @param dataScopeList 数据权限集合
     * @return 单个数据权限对象
     */
    private DataScopeDto getMatchedDataScope(List<DataScopeDto> dataScopeList) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            // 获取请求对象失败
            log.error("请求异常");
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String method = request.getMethod();
        final String requestUri = request.getRequestURI();
        log.info("请求地址: {}", requestUri);
        if (ObjectUtils.isEmpty(dataScopeList)) {
            log.info("当前用户数据权限为空");
            return null;
        }
        // 请求路径可能被网关处理过 /mgr/xx/** -> /**
        // 处理请求前缀
        final String uri = this.addUriPrefix(requestUri);
        log.info("匹配原始请求地址: {}", uri);
        // 此处的路径匹配度已经过排序 {@link cn.bctools.auth.service.impl.DataRoleServiceImpl#queryUserPermission}
        DataScopeDto dataScopeDto = dataScopeList.stream()
                .filter(e -> method.equals(e.getDataApi().getType()))
                .filter(e -> MATCHER.match(e.getDataApi().getApi(), uri))
                .findAny().orElse(null);
        if (ObjectNull.isNotNull(dataScopeDto)) {
            log.info("最匹配的数据权限路径: {}", dataScopeDto.getDataApi().getApi());
        } else {
            log.info("没有匹配的数据权限");
        }
        return dataScopeDto;
    }

    /**
     * 拼接uri前缀
     *
     * @param uri 原本的uri
     * @return 完整的uri
     */
    private String addUriPrefix(String uri) {
        String appName = SpringContextUtil.getApplicationContextName();
        if (StringUtils.isNotBlank(appName)) {
            int length = appName.length();
            if (appName.endsWith(MGR)) {
                uri = "/mgr/" + appName.substring(0, length - MGR.length()) + uri;
            }
            if (appName.endsWith(BIZ)) {
                uri = "/api/" + appName.substring(0, length - BIZ.length()) + uri;
            }
        }
        return uri;
    }

}
