package cn.bctools.core.seata.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 初始化数据表
 *
 * @author Administrator
 */
@Slf4j
public class SeataConfiguration {

    @Autowired
    public DataSource dataSource;

    public static final String UNDO_LOG_SQL = "CREATE TABLE IF NOT EXISTS `undo_log` (" +
            "`branch_id` bigint(20) NOT NULL,  " +
            "`xid` varchar(100) NOT NULL,      " +
            "`context` varchar(128) NOT NULL,  " +
            "`rollback_info` longblob NOT NULL," +
            "`log_status` int(11) NOT NULL,    " +
            "`log_created` datetime NOT NULL,  " +
            "`log_modified` datetime NOT NULL, " +
            "`ext` varchar(100) DEFAULT NULL,  " +
            "PRIMARY KEY (`branch_id`,`xid`) USING BTREE," +
            "UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='seata-undo日志';";

    /**
     * 判断当前数据库是否有undo_log 该表，如果没有，
     * 创建该表 undo_log 为seata 记录事务sql执行的记录表 第二阶段时，如果confirm会清除记录，如果是cancel 会根据记录补偿原数据
     */
    @PostConstruct
    public void detectTable() {
        try {
            log.info("[seata]初始化undo_log表");
            dataSource.getConnection().prepareStatement(UNDO_LOG_SQL).execute();
        } catch (SQLException e) {
            log.error("[seata]undo_log表创建异常", e);
        }
    }

}