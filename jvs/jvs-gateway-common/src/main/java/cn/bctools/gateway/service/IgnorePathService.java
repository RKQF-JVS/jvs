package cn.bctools.gateway.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.gateway.entity.GatewayIgnorePathPo;
import cn.bctools.gateway.mapper.GatewayIgnorePathMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 */
@Slf4j
@Service
public class IgnorePathService {

    @Resource
    GatewayIgnorePathMapper ignorePathMapper;

    static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public Boolean getIgnorePath(String path) {
        //如果是外部开发地址，则直接放开
        List<String> list = list();
        for (String api : list) {
            //匹配是否开放
            if (PATH_MATCHER.matchStart(api, path)) {
                return true;
            }
        }
        return false;
    }

    public List<String> list() {
        return ignorePathMapper.selectList(Wrappers.query()).stream().map(GatewayIgnorePathPo::getPath).collect(Collectors.toList());
    }

}
