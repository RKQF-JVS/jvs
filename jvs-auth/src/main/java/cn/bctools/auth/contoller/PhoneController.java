package cn.bctools.auth.contoller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.bctools.auth.component.SmsEmailComponent;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.service.UserService;
import cn.bctools.common.utils.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/phone")
public class PhoneController {

    UserService userService;
    SmsEmailComponent smsComponent;

    /**
     * 发送验证码, 用于手机号登录
     * <p>
     * 该手机号已被使用时才能发送
     *
     * @param phone 手机号
     * @return 发送结果
     */
    @GetMapping("/sms/verification/{phone}")
    public R<Boolean> verifyPhone(@PathVariable String phone) {
        // 判断手机号是否存在
        userService.phone(phone);
        smsComponent.sendPhoneCode(phone);
        return R.ok(true, "发送成功");
    }

    /**
     * 发送验证码, 注册
     * <p>
     * 该手机号未被使用时才能发送
     *
     * @param phone 手机号
     * @return 发送结果
     */
    @GetMapping("/sms/register/{phone}")
    public R<Boolean> register(@PathVariable String phone) {
        // 手机号去重校验
        int count = userService.count(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
        if (count != 0) {
            return R.failed("手机号已被使用, 可直接登录");
        }
        smsComponent.sendPhoneCode(phone);
        return R.ok(true, "发送成功");
    }

    /**
     * 发送验证码, 用于手机号绑定
     * <p>
     * 该手机号未被使用时才能发送
     *
     * @param phone 手机号
     * @return 发送结果
     */
    @GetMapping("/sms/bind/{phone}")
    public R<Boolean> bindPhone(@PathVariable String phone) {
        // 手机号去重校验
        int count = userService.count(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
        if (count != 0) {
            return R.failed("手机号已被使用");
        }
        smsComponent.sendPhoneCode(phone);
        return R.ok(true, "发送成功");
    }

}
