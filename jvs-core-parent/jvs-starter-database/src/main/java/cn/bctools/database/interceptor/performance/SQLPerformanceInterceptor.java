package cn.bctools.database.interceptor.performance;

import cn.hutool.db.sql.SqlFormatter;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.database.property.SqlProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.*;

/**
 * 性能分析拦截器，用于输出每条 SQL 语句及其执行时间
 *
 * @author guojing
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@RefreshScope
@Data
@Slf4j
public class SQLPerformanceInterceptor implements Interceptor {

    private static final String DRUID_POOLED_PREPARED_STATEMENT = "com.alibaba.druid.pool.DruidPooledPreparedStatement";
    private static final String T4C_PREPARED_STATEMENT = "oracle.jdbc.driver.T4CPreparedStatement";
    private static final String ORACLE_PREPARED_STATEMENT_WRAPPER = "oracle.jdbc.driver.OraclePreparedStatementWrapper";

    private Method oracleGetOriginalSqlMethod;
    private Method druidGetSqlMethod;

    public SQLPerformanceInterceptor() {
    }

    @Resource
    SqlProperties sqlProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //如果配置为不开启,则直接返回执行结果
        if (!sqlProperties.isLog()) {
            return invocation.proceed();
        }
        String originalSql = getSql(invocation);
        // 计算执行 SQL 耗时
        long start = SystemClock.now();
        //执行获取结果
        Object result = invocation.proceed();
        //初始化执行计划结果
        if (sqlProperties.isExplainIs()) {
            if (originalSql.trim().startsWith("select") || originalSql.trim().startsWith("SELECT")) {
                JdbcTemplate jdbcTemplate = SpringContextUtil.getBean(JdbcTemplate.class);
                if (ObjectNull.isNotNull(jdbcTemplate)) {
                    //在此处将sql用explain  执行一次，将结果传递过去、
                    List<Map<String, Object>> accessType = this.getAccessType(originalSql, jdbcTemplate);
                    long end = SystemClock.now();
                    long timing = end - start;
                    try {
                        //保存日志记录
                        logRecord(originalSql, invocation.getTarget(), accessType, start, end, timing);
                    } catch (Exception exception) {
                        log.error("保存SQL日志记录出错", exception);
                    }
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> getAccessType(String originalSql, JdbcTemplate jdbcTemplate) {
        String explainSql = "Explain " + originalSql;
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(explainSql);
        return mapList;
    }

    /**
     * 记录日志
     *
     * @param originalSql 执行的SQL
     * @param targetR     传递的参数对象
     * @param start       开始的时间戳
     * @param end         结束的时间戳
     * @param timing      消耗时间
     */
    @Async
    public void logRecord(String originalSql, Object targetR, List<Map<String, Object>> accessType, long start, long end, long timing) {
        // 格式化 SQL 打印执行结果
        Object target = PluginUtils.realTarget(targetR);
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
//        格式化SQL语句,默认不格式化
        originalSql = SqlFormatter.format(originalSql);
        StringBuilder formatSql = new StringBuilder()
                .append(" Time:").append(timing)
                .append(" ms - ID:").append(ms.getId())
                .append(StringPool.NEWLINE).append("Execute SQL:")
                .append(originalSql).append(StringPool.NEWLINE);
        //获取项目名
        String applicationContextName = SpringContextUtil.getApplicationContextName();
        //如果执行未超过配置时间，则直接打印执行SQL，如果超过了给出提示
        boolean goodSql = timing < sqlProperties.getMaxTime();
        if (goodSql) {
            log.debug(formatSql.toString());
        } else {
            log.error("请优化SQL! The SQL execution time is too large, please optimize ! SQL:  【" + formatSql.toString() + "】 consumingTime:{},  explus:{}", timing, accessType);
        }
    }

    /**
     * 拼装SQL
     *
     * @param invocation
     * @return
     */
    private String getSql(Invocation invocation) {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);
        try {
            statement = (Statement) stmtMetaObj.getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }
        if (stmtMetaObj.hasGetter("delegate")) {
            //Hikari
            try {
                statement = (Statement) stmtMetaObj.getValue("delegate");
            } catch (Exception ignored) {

            }
        }

        String originalSql = null;
        String stmtClassName = statement.getClass().getName();
        if (DRUID_POOLED_PREPARED_STATEMENT.equals(stmtClassName)) {
            try {
                if (druidGetSqlMethod == null) {
                    Class<?> clazz = Class.forName(DRUID_POOLED_PREPARED_STATEMENT);
                    druidGetSqlMethod = clazz.getMethod("getSql");
                }
                Object stmtSql = druidGetSqlMethod.invoke(statement);
                if (stmtSql instanceof String) {
                    originalSql = (String) stmtSql;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (T4C_PREPARED_STATEMENT.equals(stmtClassName)
                || ORACLE_PREPARED_STATEMENT_WRAPPER.equals(stmtClassName)) {
            try {
                if (oracleGetOriginalSqlMethod != null) {
                    Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                    if (stmtSql instanceof String) {
                        originalSql = (String) stmtSql;
                    }
                } else {
                    Class<?> clazz = Class.forName(stmtClassName);
                    oracleGetOriginalSqlMethod = getMethodRegular(clazz, "getOriginalSql");
                    if (oracleGetOriginalSqlMethod != null) {
                        //OraclePreparedStatementWrapper is not a public class, need set this.
                        oracleGetOriginalSqlMethod.setAccessible(true);
                        if (null != oracleGetOriginalSqlMethod) {
                            Object stmtSql = oracleGetOriginalSqlMethod.invoke(statement);
                            if (stmtSql instanceof String) {
                                originalSql = (String) stmtSql;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //ignore
            }
        }
        if (originalSql == null) {
            originalSql = statement.toString();
        }
        originalSql = originalSql.replaceAll("[\\s]+", StringPool.SPACE);
        int index = indexOfSqlStart(originalSql);
        if (index > 0) {
            originalSql = originalSql.substring(index);
        }
        return originalSql;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * 获取此方法名的具体 Method
     *
     * @param clazz      class 对象
     * @param methodName 方法名
     * @return 方法
     */
    public Method getMethodRegular(Class<?> clazz, String methodName) {
        if (Object.class.equals(clazz)) {
            return null;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return getMethodRegular(clazz.getSuperclass(), methodName);
    }

    /**
     * 获取sql语句开头部分
     *
     * @param sql ignore
     * @return ignore
     */
    private int indexOfSqlStart(String sql) {
        String upperCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(upperCaseSql.indexOf("SELECT "));
        set.add(upperCaseSql.indexOf("UPDATE "));
        set.add(upperCaseSql.indexOf("INSERT "));
        set.add(upperCaseSql.indexOf("DELETE "));
        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        return list.get(0);
    }
}
