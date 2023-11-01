package cn.bctools.common.utils.sensitive;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.DesensitizedUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 敏感数据处理声明工具类，目前已经支持五种不同的数据脱敏，如果需要处理，可以自行调用 {@linkplain SensitiveInfoUtils#put(String, Function)} 实现，或者覆盖，
 * 已知名称为 idcard | realname| bankcard| mobile| phone  可以直接覆盖之前的数据
 * 此功能在业务层进行操作，而并非在网关层统一操作。所以打印出来的日志也会同样已经处理过脱敏了。
 * 但必须要自定义logback.xml 的  conversionRule 配置如下：
 * <数据脱敏操作,如果项目中不需要脱敏可以不处理，此处是做日志打印时的脱敏操作
 * <conversionRule conversionWord="msg" converterClass="cn.bctools.common.utils.sensitive.SensitiveDataConverter"> </conversionRule>
 *
 * @author gj
 */
public class SensitiveInfoUtils {

    /**
     * 默认的脱敏方法操作
     */
    static Function DEFAULT_SENSITIVE_FUNCTION = (e) -> JSONObject.parseObject(SensitiveDataConverter.processor(e), e.getClass());

    /**
     * 获取默认的解析器
     *
     * @param <T> 具体的实现类
     * @return
     */
    public static <T> Function<T, T> get() {
        return DEFAULT_SENSITIVE_FUNCTION;
    }

    /**
     * 敏感词的映射关系
     */
    static Map<String, Function<String, String>> sensitiveKey = new HashMap<>();

    /**
     * 可覆盖或自定义脱敏操作
     * 支持动态添加删减去
     *
     * @param key      脱敏的key
     * @param function 处理的方法
     */
    public static void put(String key, Function<String, String> function) {
        sensitiveKey.put(key, function);
    }

    /**
     * 获取此对象可以直接对脱敏key，value，进行业务处理
     *
     * @return
     */
    public static Map<String, Function<String, String>> getSensitiveKey() {
        return sensitiveKey;
    }

    public static List<Dict> map = new ArrayList<>();

    /**
     *
     * 默认的映射关系
     */
    static {
        sensitiveKey.put("idcard", (e) -> DesensitizedUtil.idCardNum(String.valueOf(e), 1, 2));
        sensitiveKey.put("realname", (e) -> DesensitizedUtil.chineseName(String.valueOf(e)));
        sensitiveKey.put("bankcard", (e) -> DesensitizedUtil.bankCard(String.valueOf(e)));
        sensitiveKey.put("mobile", (e) -> DesensitizedUtil.mobilePhone(String.valueOf(e)));
        sensitiveKey.put("phone", (e) -> DesensitizedUtil.mobilePhone(String.valueOf(e)));
        sensitiveKey.put("address", (e) -> DesensitizedUtil.address(String.valueOf(e), 8));
        sensitiveKey.put("email", (e) -> DesensitizedUtil.email(String.valueOf(e)));
        sensitiveKey.put("all", (e) -> DesensitizedUtil.address(String.valueOf(e), e.length()));

        map.add(Dict.create().set("label", "所有文字").set("value", "all"));
        map.add(Dict.create().set("label", "身份证").set("value", "idcard"));
        map.add(Dict.create().set("label", "姓名").set("value", "realname"));
        map.add(Dict.create().set("label", "银行卡号").set("value", "bankcard"));
        map.add(Dict.create().set("label", "手机号").set("value", "phone"));
        map.add(Dict.create().set("label", "地址").set("value", "address"));
        map.add(Dict.create().set("label", "邮箱").set("value", "email"));

    }


    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }

    public static String processor(String key, String submsg) {
        if (sensitiveKey.containsKey(key)) {
            return String.valueOf(sensitiveKey.get(key).apply(submsg));
        }
        //返回自己
        return submsg;
    }
}
