package cn.bctools.gateway.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.RequestUrlDto;
import cn.bctools.common.entity.dto.ScannerDto;
import cn.bctools.common.utils.PasswordUtil;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.entity.TenantPo;
import cn.bctools.gateway.mapper.ApplyMapper;
import cn.bctools.gateway.mapper.TenantMapper;
import cn.bctools.oauth2.utils.ScannerApplicationContextAware;
import cn.bctools.redis.utils.RedisUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@RestController
@RequestMapping
@AllArgsConstructor
public class GatewayController {

    ApplyMapper applyMapper;
    TenantMapper tenantMapper;
    RedisUtils redisUtils;
    StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_GATEWAY_URL = "gateway:url";

    /**
     * 上报数据请求链接数据
     * 各个微服务在启动时会调用此接口
     * <p>
     * 详情见{@link ScannerApplicationContextAware#setApplicationContext}
     */
    @PostMapping("/gateway/handler/mapping")
    public Mono<R<Boolean>> handlerMapping(@RequestBody String data) {
        // 将注册路由数据存放到Redis中
        String decodedPassword = PasswordUtil.decodedPassword(data.trim(), SpringContextUtil.getKey());
        ScannerDto scannerDto = JSONObject.parseObject(decodedPassword, ScannerDto.class);
        // 校验应用信息
        Apply apply = applyMapper.selectOne(Wrappers.<Apply>lambdaQuery().eq(Apply::getAppKey, scannerDto.getClientId()));
        if (Objects.isNull(apply)) {
            return Mono.just(R.failed("非法注册"));
        }
        // 校验应用密钥
        String secret = apply.getAppSecret();
        boolean checkSecret = BCrypt.checkpw(scannerDto.getClientSecret(), secret);
        if (!checkSecret) {
            return Mono.just(R.failed("非法注册"));
        }
        // Redis缓存路由数据
        List<RequestUrlDto> urlList = scannerDto.getList();
        if (ObjectUtils.isNotEmpty(urlList)) {
            List<String> list = urlList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
            String[] urlArray = ArrayUtil.toArray(list, String.class);
            redisTemplate.boundSetOps(REDIS_KEY_GATEWAY_URL).add(urlArray);
        }
        // Redis缓存数据权限数据
        redisUtils.set((SysConstant.DATASCOPE + scannerDto.getApplicationName()), scannerDto.getDataDictDtos());
        return Mono.just(R.ok(true));
    }

    /**
     * 查询有哪些URL地址
     */
    @GetMapping("/gateway/handler/mapping")
    public Mono<R<List<String>>> getHandlerMapping() {
        // 从Redis缓存中获取接口地址数据
        Set<String> members = redisTemplate.boundSetOps(REDIS_KEY_GATEWAY_URL).members();
        if (ObjectUtils.isEmpty(members)) {
            return Mono.just(R.ok(Collections.emptyList()));
        }
        List<String> urlList = new ArrayList<>(members.size());
        // 模糊查询
        for (String urlJson : members) {
            RequestUrlDto urlDTO = JSONUtil.toBean(urlJson, RequestUrlDto.class);
            String api = urlDTO.getApi();
            urlList.add(String.format("%s %s", urlDTO.getMethod().name().toLowerCase(), api));
        }
        Collections.sort(urlList);
        return Mono.just(R.ok(urlList));
    }

    /**
     * 获取邀请码信息
     */
    @GetMapping("/gateway/invite")
    public Mono invite(@RequestParam String key) {
        if (redisUtils.hasKey(key)) {
            UserDto o = (UserDto) redisUtils.get(key);
            //替换用户的所有其它信息
            TenantPo tenantPo = tenantMapper.selectById(o.getTenantId());
            o = new UserDto().setHeadImg(o.getHeadImg()).setRealName(o.getRealName());
            Dict set = Dict.create().set("user", o).set("tenant", tenantPo);
            return Mono.just(R.ok(set));
        }
        return Mono.just(R.failed("邀请码失效"));
    }

}
