package cn.bctools.gateway.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.gateway.entity.Permission;
import cn.bctools.gateway.mapper.PermissionMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Service
@AllArgsConstructor
public class PermissionService {

    PermissionMapper permissionMapper;

    static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public List<String> getIgnorePath(String path) {
        //如果是外部开发地址，则直接放开
        return list().stream().filter(e -> PATH_MATCHER.matchStart(e, path)).collect(Collectors.toList());
    }

    private List<String> list() {
        List<Permission> permissions = permissionMapper.selectList(Wrappers.query());
        return permissions.stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getApi()))
                .flatMap(e -> e.getApi().stream())
                .collect(Collectors.toList());
    }

}
