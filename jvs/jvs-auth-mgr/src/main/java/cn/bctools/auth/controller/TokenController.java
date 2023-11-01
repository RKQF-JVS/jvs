package cn.bctools.auth.controller;

import cn.bctools.auth.service.ApplyService;
import cn.bctools.common.constant.SysConstant;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.R;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.log.annotation.Log;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/token")
public class TokenController {

    TokenStore tokenStore;
    RedisUtils redisUtils;
    ApplyService applyService;

    @Log
    @ApiOperation(value = "后台强制退出", notes = "由管理员在后台管理系统中直接退出系统")
    @DeleteMapping("/{token}")
    public R forceLogout(@PathVariable("token") String token) {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
        if (accessToken == null || StrUtil.isBlank(accessToken.getValue())) {
            return R.ok(Boolean.TRUE, "退出失败，token 无效");
        }
        // 清空access token
        tokenStore.removeAccessToken(accessToken);
        // 清空 refresh token
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.removeRefreshToken(refreshToken);
        tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        return R.ok();
    }

    @Log
    @ApiOperation(value = "根据应用ID查询此应用下登录的人", notes = "同一个人可以在不同的应用下进行同时登录")
    @GetMapping("/all")
    public R tokenAll(@RequestParam(required = false) String appId) {
        String tenantId = TenantContextHolder.getTenantId();
        List<OAuth2AccessToken> collect = tokenStore.findTokensByClientId(appId).stream()
                .distinct()
                .filter(e -> redisUtils.exists(SysConstant.JVS_AUTH + "access:" + e.getValue()))
                .filter(e -> {
                    try {
                        UserDto userDto = (UserDto) e.getAdditionalInformation().get("userDto");
                        if (userDto.getTenantId().equals(tenantId)) {
                            return true;
                        }
                        return false;
                    } catch (Exception exception) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        String data = JSONObject.toJSONString(collect);
        return R.ok(JSONObject.parseArray(data));
    }

    @Log
    @ApiOperation(value = "获取所有应用", notes = "获取所有应用")
    @GetMapping("/app/all")
    public R<List<Apply>> app() {
        List<Apply> applies = applyService.list(Wrappers.query(new Apply().setEnable(true)).lambda().select(Apply::getId, Apply::getName, Apply::getAppKey));
        return R.ok(applies);
    }

}
