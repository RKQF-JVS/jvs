package cn.bctools.gateway.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

/**
 * JSON处理工具类
 *
 * @author lieber
 */
public enum JsonUtil {

    /**
     * 实例
     */
    INSTANCE;

    /**
     * json对象字符串开始标记
     */
    private final static String JSON_OBJECT_START = "{";

    /**
     * json对象字符串结束标记
     */
    private final static String JSON_OBJECT_END = "}";

    /**
     * json数组字符串开始标记
     */
    private final static String JSON_ARRAY_START = "[";

    /**
     * json数组字符串结束标记
     */
    private final static String JSON_ARRAY_END = "]";


    /**
     * 判断字符串是否json对象字符串
     *
     * @param val 字符串
     * @return true/false
     */
    public boolean isJsonObj(String val) {
        if (StringUtils.isEmpty(val)) {
            return false;
        }
        val = val.trim();
        if (val.startsWith(JSON_OBJECT_START) && val.endsWith(JSON_OBJECT_END)) {
            try {
                JSONObject.parseObject(val);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否json数组字符串
     *
     * @param val 字符串
     * @return true/false
     */
    public boolean isJsonArr(String val) {
        if (StringUtils.isEmpty(val)) {
            return false;
        }
        val = val.trim();
        if (StringUtils.isEmpty(val)) {
            return false;
        }
        val = val.trim();
        if (val.startsWith(JSON_ARRAY_START) && val.endsWith(JSON_ARRAY_END)) {
            try {
                JSONObject.parseArray(val);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断对象是否是json对象
     *
     * @param obj 待判断对象
     * @return true/false
     */
    public boolean isJsonObj(Object obj) {
        String str = JSONObject.toJSONString(obj);
        return this.isJsonObj(str);
    }

    /**
     * 判断字符串是否json字符串
     *
     * @param str 字符串
     * @return true/false
     */
    public boolean isJson(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return this.isJsonObj(str) || this.isJsonArr(str);
    }
}
