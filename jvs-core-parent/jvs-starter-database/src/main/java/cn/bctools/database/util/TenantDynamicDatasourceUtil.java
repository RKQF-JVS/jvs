package cn.bctools.database.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.getter.DefaultTableFieldGetter;
import cn.bctools.database.property.SqlProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ZhuXiaoKang
 * @Description: 多租户动态初始化数据源工具类
 * @Date: 2021/11/03 15:40
 */
@Slf4j
public class TenantDynamicDatasourceUtil {

    private static String createDatabasesSql = "CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;";
    private static String createTableTemplate = "CREATE TABLE IF NOT EXISTS %s.`";
    private static String insertTemplate = "INSERT INTO %s.`";
    public static final String NAME_JOINER = "_";

    /**
     * 初始化数据sql脚本文件名（resources/init.sql）
     * 脚本模板：INSERT INTO `表名` (字段) VALUES (值);
     */
    private static final String INIT_DATA_FILE_NAME = "init.sql";

    /**
     * 已初始化库缓存
     * Set<库名>
     */
    private static Set<String> databaseInitialized = new HashSet<>();

    private TenantDynamicDatasourceUtil() {
    }

    /**
     * 初始化库表
     *
     * @return true-成功|忽略，false-失败
     */
    public static boolean init() {
        SqlProperties sqlProperties = SpringContextUtil.getBean(SqlProperties.class);
        if (!sqlProperties.isDynamicTenantDatabase()) {
            return true;
        }
        String tenantId = TenantContextHolder.getTenantId();
        String databaseName = "";
        DataSource dataSource = SpringContextUtil.getBean(DataSource.class);
        try (Connection connection = dataSource.getConnection()){
            // 创建数据库
            String url = connection.getMetaData().getURL();
            DatabaseInfo info = DatabaseUtils.parseUrl(url);
            databaseName = info.getDatabaseName() + NAME_JOINER +  tenantId;
            String format = String.format(createDatabasesSql, databaseName);
            if (Boolean.FALSE.equals(databaseInitialized.contains(databaseName))) {
                log.info("初始化数据库{}开始", databaseName);
                // 创建数据库
                dataSource.getConnection().createStatement().execute(format);
                // 创建表结构
                initTable(databaseName, connection);
                // 初始化数据
                initData(databaseName, connection);
                databaseInitialized.add(databaseName);
                log.info("初始化数据库{}结束", databaseName);
            }
            return true;
        } catch (SQLException throwables) {
            log.error("初始化数据库失败. 租户: {}, 库名: {}, exception: {}", tenantId, databaseName, throwables.getMessage());
            return false;
        }
    }

    /**
     * 初始化表
     * @param conn
     */
    public static void initTable(String databaseName, Connection conn) {
        DefaultTableFieldGetter.getTableList().forEach(tableName -> {
            String sql = "SHOW CREATE TABLE " + tableName;
            try {
                ResultSet resultSet = conn.createStatement().executeQuery(sql);
                while(resultSet.next()) {
                    String createTableSql = resultSet.getString(2).replace("CREATE TABLE `", String.format(createTableTemplate, databaseName)) + ";";
                    conn.createStatement().execute(createTableSql);
                }
            } catch (SQLException throwables) {
                log.error("初始化表失败. 库名: {}, 表名: {}, exception: {}", databaseName, tableName, throwables.getMessage());
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param databaseName
     * @param conn
     */
    public static void initData(String databaseName, Connection conn) {
        try {
            String defaultDataSql = ResourceUtil.readUtf8Str(INIT_DATA_FILE_NAME);
            if (StringUtils.isBlank(defaultDataSql)) {
                return;
            }
            defaultDataSql = defaultDataSql.replace("INSERT INTO `", String.format(insertTemplate, databaseName));

            ScriptRunner runner = new ScriptRunner(conn);
            runner.setSendFullScript(Boolean.FALSE);
            runner.runScript(StrUtil.getReader(defaultDataSql));
        } catch (Exception e) {
            log.error("初始化数据异常。exception: {}", e.getMessage());
        }

    }
}
