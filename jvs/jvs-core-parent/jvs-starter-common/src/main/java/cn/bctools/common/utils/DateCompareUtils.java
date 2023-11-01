package cn.bctools.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 日期比较工具类
 *
 * @Author: GuoZi
 */
public class DateCompareUtils {

    public static final long DEFAULT_RANGE_DAY = 30 * 24 * 60 * 60;

    public static boolean isRecentOneMonth(LocalDateTime start) {
        if (Objects.isNull(start)) {
            return false;
        }
        Duration between = Duration.between(start, LocalDateTime.now());
        return between.getSeconds() <= DEFAULT_RANGE_DAY;
    }

}
