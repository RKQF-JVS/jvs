package cn.bctools.database.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Mysql查询条件工具类
 *
 * @Author: GuoZi
 */
public class WrapperUtil {

    /**
     * 模糊查询参数转义
     *
     * @param data 参数
     * @return 转义后的参数
     */
    public static String parseLike(String data) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        data = data.replace("%", "\\%");
        return data.replace("_", "\\_");
    }

}
