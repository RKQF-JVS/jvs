package cn.bctools.dynamic.seata;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 创建seata的undo_log表
 * @author auto
 */
@Slf4j
@AllArgsConstructor
@Data
public class UndoLogTableCreator implements Runnable {

    /**
     * 数据源信息
     */
    private DataSource dataSource;

    /**
     * 创建undo_log表的ddl
     */
    private static final String UNDO_LOG = "CREATE TABLE `undo_log` (\n" +
            "  `branch_id` bigint(20) NOT NULL,\n" +
            "  `xid` varchar(100) NOT NULL,\n" +
            "  `context` varchar(128) NOT NULL,\n" +
            "  `rollback_info` longblob NOT NULL,\n" +
            "  `log_status` int(11) NOT NULL,\n" +
            "  `log_created` datetime NOT NULL,\n" +
            "  `log_modified` datetime NOT NULL,\n" +
            "  `ext` varchar(100) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`branch_id`,`xid`) USING BTREE,\n" +
            "  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='seata-undo日志';";

    /**
     * 校验undo_log表是否存在的ddl
     */
    private static final String CHECK_TABLE_EXISTS = "SELECT TABLE_NAME FROM information_schema.`TABLES` WHERE TABLE_SCHEMA='%s' AND TABLE_TYPE='BASE TABLE' AND TABLE_NAME='undo_log'";

    @Override
    public void run() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        try {
            String url = null;
            if (dataSource instanceof DruidDataSource) {
                url = ((DruidDataSource) dataSource).getUrl();
            } else if (dataSource instanceof HikariDataSource) {
                url = ((HikariDataSource) dataSource).getJdbcUrl();
            } else if (dataSource instanceof MysqlDataSource) {
                url = ((MysqlDataSource) dataSource).getURL();
            } else if (dataSource instanceof DataSourceProxy) {
                url = ((DataSourceProxy) dataSource).getResourceId();
            }
            String databaseName = Objects.requireNonNull(url).split("/")[3].split("\\?")[0];
            jdbcTemplate.queryForObject(String.format(CHECK_TABLE_EXISTS, databaseName), String.class);
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.execute(UNDO_LOG);
            log.info("seata undo_log table not exist,auto create on master");
        } catch (Throwable e) {
            log.error("create seata undo_log error:{}", e.getMessage());
        }
    }
}
