package cn.bctools.dynamic.utl;

import cn.hutool.core.util.HexUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicGroupDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.entity.DataSourceInfo;
import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 动态数据源维护工具
 *
 * @author auto
 */
@Slf4j
@Data
@UtilityClass
public class DynamicDataSourceUtils {
    /**
     * jdbc后缀
     */
    public static String JDBC_URL_SUFFIX = "?connectTimeout=60000&socketTimeout=60000&autoReconnect=true&maxReconnects=3&failOverReadOnly=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true";
    /**
     * jdbc format
     */
    public static String JDBC_URL_FORMAT = "jdbc:mysql://%s:%s/%s";

    private DynamicRoutingDataSource dynamicRoutingDataSource;

    /**
     * 获取数据源key
     *
     * @param dataSourceInfo 数据源
     * @return key
     */
    public static String generateKey(DataSourceInfo dataSourceInfo) {
        return generateKey(
                dataSourceInfo.getIp(),
                dataSourceInfo.getPort(),
                dataSourceInfo.getDatabaseName(),
                dataSourceInfo.getUsername(),
                dataSourceInfo.getPassword());
    }

    /**
     * 获取数据源key
     *
     * @param ip           数据源ip
     * @param port         数据源port
     * @param databaseName 数据库名称
     * @param username     数据源账号
     * @param password     数据源密码
     * @return key
     */
    public static String dataSourceKeyAndAddDataSource(String ip, String port, String databaseName, String username, String password) {
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setUsername(username)
                .setPassword(password)
                .setIp(ip)
                .setPort(port)
                .setDatabaseName(databaseName);
        addDataSource(dataSourceInfo);
        return generateKey(ip, port, databaseName, username, password);
    }

    /**
     * 获取数据源key
     *
     * @param ip           数据源ip
     * @param port         数据源port
     * @param databaseName 数据库名称
     * @param username     数据源账号
     * @param password     数据源密码
     * @return key
     */
    public static String generateKey(String ip, String port, String databaseName, String username, String password) {
        return HexUtil.encodeHexStr(String.format("%s:%s:%s:%s:%s", ip, port, databaseName, username, password));
    }

    /**
     * 根据数据源key获取数据源连接信息
     *
     * @param key 数据源key
     * @return key
     */
    public static DataSourceInfo parseKey(String key) {
        byte[] bytes = HexUtil.decodeHex(key);
        String s = new String(bytes);
        String[] split = s.split(":");
        if (split.length != 5) {
            log.error("未知格式的数据源key: {}", key);
            throw new BusinessException("当前数据源信息解析异常");
        }
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setIp(split[0]);
        dataSourceInfo.setPort(split[1]);
        dataSourceInfo.setDatabaseName(split[2]);
        dataSourceInfo.setUsername(split[3]);
        dataSourceInfo.setPassword(split[4]);
        return dataSourceInfo;
    }

    /**
     * 获取服务加载的所有数据源DataSource
     *
     * @return Map<数据源key, DataSource>
     */
    public Map<String, DataSource> gets() {
        return getDynamicRoutingDataSource().getCurrentDataSources();
    }

    /**
     * 根据数据源key获取数据源DataSource
     *
     * @param dataSourceKey 数据源key
     * @return 数据源DataSource
     */
    public DataSource get(String dataSourceKey) {
        return getDynamicRoutingDataSource().getDataSource(dataSourceKey);
    }

    /**
     * 移除加载的数据源
     *
     * @param e 数据源信息
     */
    public void removeDataSource(DataSourceInfo e) {
        String key = generateKey(e);
        try {
            getDynamicRoutingDataSource().removeDataSource(key);
        } catch (Throwable ex) {
            log.error("移除动态数据源错误：{}，{}", key, ex.getMessage());
        }
    }

    /**
     * 添加数据源
     *
     * @param dataSourceInfo 数据源信息
     */
    public void addDataSource(DataSourceInfo dataSourceInfo) {
        addDataSource(dataSourceInfo, false);
    }

    /**
     * 添加数据源
     *
     * @param dataSourceInfo 数据源信息
     * @param throwEx        添加失败时是否抛出异常
     */
    public void addDataSource(DataSourceInfo dataSourceInfo, boolean throwEx) {
        //获取当前数据源
        Map<String, DynamicGroupDataSource> dataSourceMap = getDynamicRoutingDataSource().getCurrentGroupDataSources();
        String key = generateKey(dataSourceInfo);
        if (dataSourceMap.containsKey(key)) {
            //存在 不做操作
            return;
        }
        //不存在 添加
        Throwable exception = null;
        Connection connection = null;
        try {
            // 校验是否可连接
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.configFromPropety(getDataSourceProp(dataSourceInfo));
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setURL(String.format(JDBC_URL_FORMAT, dataSourceInfo.getIp(), dataSourceInfo.getPort(), dataSourceInfo.getDatabaseName()).concat(JDBC_URL_SUFFIX));
            mysqlDataSource.setUser(dataSourceInfo.getUsername());
            mysqlDataSource.setPassword(dataSourceInfo.getPassword());
            connection = mysqlDataSource.getConnection();
            // 连接未报错, 添加
            getDynamicRoutingDataSource().addDataSource(key, dataSource);
            log.info("添加动态数据源成功：ip:{},port:{},username:{},database:{}", dataSourceInfo.getIp(), dataSourceInfo.getPort(), dataSourceInfo.getUsername(), dataSourceInfo.getDatabaseName());
        } catch (Throwable ex) {
            exception = ex;
            log.error("添加动态数据源错误：ip:{},port:{},username:{},database:{},error:{}", dataSourceInfo.getIp(), dataSourceInfo.getPort(), dataSourceInfo.getUsername(), dataSourceInfo.getDatabaseName(), ex.getMessage(), ex);
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
        if (throwEx && ObjectNull.isNotNull(exception)) {
            throw new BusinessException("添加动态数据源错误");
        }
    }

    /**
     * 数据源信息转换成spring配置Properties
     *
     * @param e 数据源信息
     * @return Properties
     */
    public static Properties getDataSourceProp(DataSourceInfo e) {
        Properties properties = new Properties();
        String url = String.format(DynamicDataSourceUtils.JDBC_URL_FORMAT, e.getIp(), e.getPort(), e.getDatabaseName()).concat(DynamicDataSourceUtils.JDBC_URL_SUFFIX);
        properties.setProperty("druid.url", url);
        properties.setProperty("druid.username", e.getUsername());
        properties.setProperty("druid.password", e.getPassword());
        properties.setProperty("druid.driverClassName", Driver.class.getName());
        return properties;
    }

    /**
     * 获取动态数据源Routing
     *
     * @return DynamicRoutingDataSource
     */
    private DynamicRoutingDataSource getDynamicRoutingDataSource() {
        if (dynamicRoutingDataSource == null) {
            dynamicRoutingDataSource = SpringContextUtil.getBean(DynamicRoutingDataSource.class);
        }
        return dynamicRoutingDataSource;
    }
}
