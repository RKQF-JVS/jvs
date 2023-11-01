package cn.bctools.auth.contoller;

import cn.bctools.auth.login.dto.WxQrCodeCheckDto;
import cn.bctools.common.utils.R;
import cn.bctools.log.annotation.Log;
import cn.bctools.redis.utils.RedisUtils;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author ：admin
 * 获取登录二维码
 */
@RestController
@AllArgsConstructor
@RequestMapping("/wx/qr/code")
@Slf4j
public class WxQrCodeLogin {
    private final RedisUtils redisUtils;
    private WxMpService wxMpService;

    @Log
    @GetMapping("/login/{loginId}")
    @SneakyThrows
    public R<String> add(@ApiParam(value = "前端唯一id用于确认登录账号") @PathVariable String loginId) {
        WxMpQrcodeService qrcodeService = wxMpService.getQrcodeService();
        WxMpQrCodeTicket wxMpQrCodeTicket = qrcodeService.qrCodeCreateTmpTicket(loginId, 5 * 60);
        log.info("获取的ticket数据为:{}", JSONUtil.toJsonStr(wxMpQrCodeTicket));
        String s = qrcodeService.qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
        redisUtils.setExpire(loginId, "", 5L, TimeUnit.MINUTES);
        return R.ok(s);
    }

    /**
     * 用于    前端判断二维码 是否扫码成功 并进入微信公众号
     */
    @SneakyThrows
    @GetMapping("/check/{loginId}")
    public R<WxQrCodeCheckDto> getQrCode(@ApiParam(value = "前端生成的唯一id", required = true) @PathVariable String loginId) {
        WxQrCodeCheckDto wxQrCodeCheckDto = new WxQrCodeCheckDto();
        boolean exists = redisUtils.exists(loginId);
        if (exists) {
            Object o = redisUtils.get(loginId);
            if (ObjectUtils.isNotEmpty(o)) {
                wxQrCodeCheckDto.setCheckStatus(Boolean.TRUE);
            }
            return R.ok(wxQrCodeCheckDto);
        }
        wxQrCodeCheckDto.setIsPastDue(Boolean.TRUE);
        return R.ok(wxQrCodeCheckDto);
    }
}
