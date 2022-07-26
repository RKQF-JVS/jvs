package cn.bctools.database.util;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.database.entity.DatabaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: GuoZi
 */
@Slf4j
public class DatabaseUtils {

    private DatabaseUtils() {
    }

    public static DatabaseInfo parseUrl(String url) {
        DatabaseInfo info = new DatabaseInfo();
        String[] subUrl = url.split("/", 4);
        if (subUrl.length != 4) {
            log.error("数据源连接url格式异常: {}", url);
            throw new BusinessException("数据源连接url格式异常");
        }
        String[] split = subUrl[2].split(":");
        if (split.length == 1) {
            // 域名
            info.setIp(split[0]);
        } else if (split.length == 2) {
            // ip:port
            info.setIp(split[0]);
            info.setPort(split[1]);
        } else {
            log.error("数据源连接url格式异常: {}", url);
            throw new BusinessException("数据源连接url格式异常");
        }
        // 数据库名称
        String databaseName = subUrl[3].split("\\?")[0];
        if (StringUtils.isBlank(databaseName)) {
            log.error("数据源连接url格式异常: {}", url);
            throw new BusinessException("数据源连接url格式异常");
        }
        info.setDatabaseName(databaseName);
        return info;
    }

}
