package cn.bctools.common.utils.sensitive;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
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

    /**
     *
     * 默认的映射关系
     */
    static {
        sensitiveKey.put("idcard", (e) -> idCardNum(e));
        sensitiveKey.put("realname", (e) -> chineseName(e));
        sensitiveKey.put("bankcard", (e) -> bankCard(e));
        sensitiveKey.put("mobile", (e) -> mobilePhone(e));
        sensitiveKey.put("phone", (e) -> mobilePhone(e));
    }


    /**
     * [姓名] 只显示第一个汉字，其他隐藏为星号<例子：李**>
     *
     * @param fullName
     * @return
     */
    private static String chineseName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     *
     * @param idCardNum
     * @return
     */
    static String idCardNum(String idCardNum) {
        if (StringUtils.isBlank(idCardNum)) {
            return "";
        }
        String num = StringUtils.right(idCardNum, 4);
        return StringUtils.leftPad(num, StringUtils.length(idCardNum), "*");
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138******1234>
     *
     * @param num
     * @return
     */
    static String mobilePhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
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
