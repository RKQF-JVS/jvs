package cn.bctools.auth.comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 比较路径的匹配度
 * <p>
 * 校验规则
 * 1. 多个连续的双星号视为一个
 * 2. 逐层比较匹配度: 双星号 < 单星号 < 其他
 *
 * @Author: GuoZi
 */
public class UriPatternComparator implements Comparator<String> {

    @Override
    public int compare(String thisPattern, String otherPattern) {
        // 将多个连续的双星号化简为一个
        List<String> thisPatternList = this.simplify(thisPattern);
        List<String> otherPatternList = this.simplify(otherPattern);
        if (thisPatternList.equals(otherPatternList)) {
            // 化简后格式相同
            return 0;
        }
        int min = Math.min(thisPatternList.size(), otherPatternList.size());
        for (int i = 0; i < min; i++) {
            // 逐层比较匹配度
            int thisValue = PatternEnum.getValue(thisPatternList.get(i));
            int otherValue = PatternEnum.getValue(otherPatternList.get(i));
            if (thisValue != otherValue) {
                return thisValue - otherValue;
            }
        }
        // 比较完毕且长度相同时, 匹配度相同
        if (thisPatternList.size() == otherPatternList.size()) {
            return 0;
        }
        // 格式较长且包含双星号的一方, 匹配度较低
        if (thisPatternList.size() < otherPatternList.size()) {
            return PatternEnum.DOUBLE_WILDCARD.value == PatternEnum.getValue(otherPatternList.get(min)) ? 1 : -1;
        } else {
            return PatternEnum.DOUBLE_WILDCARD.value == PatternEnum.getValue(thisPatternList.get(min)) ? -1 : 1;
        }
    }

    /**
     * 化简uri匹配格式
     * <p>
     * 将连续的双星号化简为一个, 并以 / 分隔
     *
     * @param pattern uri匹配格式
     * @return 分隔后的集合
     */
    private List<String> simplify(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return Collections.emptyList();
        }
        String[] split = pattern.split("\\?")[0].split("/");
        List<String> result = new ArrayList<>();
        boolean exist = false;
        for (String str : split) {
            int value = PatternEnum.getValue(str);
            if (PatternEnum.DOUBLE_WILDCARD.value == value) {
                if (!exist) {
                    result.add(str);
                }
                exist = true;
            } else {
                result.add(str);
                exist = false;
            }
        }
        return result;
    }

    @Getter
    @AllArgsConstructor
    private enum PatternEnum {

        /**
         * 两个星号
         */
        DOUBLE_WILDCARD("**", 0),
        /**
         * 单个星号
         */
        SINGLE_WILDCARD("*", 1);

        private final String str;
        private final int value;

        public static int getValue(String str) {
            for (PatternEnum value : PatternEnum.values()) {
                if (value.str.equals(str)) {
                    return value.value;
                }
            }
            return 2;
        }

    }

}
