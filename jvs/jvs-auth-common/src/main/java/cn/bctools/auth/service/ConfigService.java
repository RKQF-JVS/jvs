package cn.bctools.auth.service;

import cn.bctools.gateway.cons.GatewayCons;
import cn.bctools.gateway.mapper.GatewayIgnoreEncodeMapper;
import cn.bctools.gateway.mapper.GatewayIgnoreXssMapper;
import cn.bctools.redis.utils.RedisUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 
 */
@Slf4j
@Service
public class ConfigService {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    GatewayIgnoreXssMapper xssMapper;
    @Autowired
    GatewayIgnoreEncodeMapper encodeMapper;

    /**
     * 刷新配置
     */
    public void refresh() {
        refreshXss();
        refreshEncode();
    }

    /**
     * 刷新配置
     */
    public void refreshXss() {
        redisUtils.del(GatewayCons.xss);
        List collect = xssMapper.selectList(Wrappers.query())
                .stream()
                .map(e -> e.getPath())
                .collect(Collectors.toList());
        redisUtils.lSetList(GatewayCons.xss, collect);
    }

    public void refreshEncode() {
        redisUtils.del(GatewayCons.encode);
        List collect = encodeMapper.selectList(Wrappers.query())
                .stream()
                .map(e -> e.getPath())
                .collect(Collectors.toList());
        redisUtils.lSetList(GatewayCons.encode, collect);
    }

}
