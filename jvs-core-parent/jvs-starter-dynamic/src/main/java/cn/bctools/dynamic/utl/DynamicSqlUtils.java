package cn.bctools.dynamic.utl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.database.entity.DataSourceInfo;
import cn.bctools.dynamic.enums.ResultType;
import cn.bctools.dynamic.enums.Token;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;
import java.util.*;

@Slf4j
public class DynamicSqlUtils {

    public static String getDataSourceKey(DataSourceInfo dataSourceInfo) {
        return DynamicDataSourceUtils.generateKey(dataSourceInfo);
    }

    public static String getDataSourceKey(String ip, String port, String username, String databaseName) {
        DataSourceInfo dataSource = new DataSourceInfo();
        dataSource.setUsername(username)
                .setIp(ip)
                .setPort(port)
                .setDatabaseName(databaseName);
        return DynamicDataSourceUtils.generateKey(dataSource);
    }

    public static String getDataSourceKeyWithAdd(DataSourceInfo dataSourceInfo) {
        return getDataSourceKeyWithAdd(
                dataSourceInfo.getIp(),
                dataSourceInfo.getPort(),
                dataSourceInfo.getDatabaseName(),
                dataSourceInfo.getUsername(),
                dataSourceInfo.getPassword());
    }

    public static String getDataSourceKeyWithAdd(String ip, String port, String databaseName, String username) {
        DataSourceInfo dataSource = new DataSourceInfo();
        dataSource.setUsername(username)
                .setIp(ip)
                .setPort(port)
                .setDatabaseName(databaseName);
        DynamicDataSourceUtils.addDataSource(dataSource);
        return DynamicDataSourceUtils.generateKey(dataSource);
    }

    public static String getDataSourceKeyWithAdd(String ip, String port, String databaseName, String username, String password) {
        DataSourceInfo dataSource = new DataSourceInfo();
        dataSource.setUsername(username)
                .setPassword(password)
                .setIp(ip)
                .setPort(port)
                .setDatabaseName(databaseName);
        DynamicDataSourceUtils.addDataSource(dataSource);
        return DynamicDataSourceUtils.generateKey(dataSource);
    }

    public static String checkSql(String sql, Map<String, Object> parameters, Token token) {
        return checkSql(sql, parameters, Collections.singletonList(token));
    }

    public static String checkSql(String sql, Map<String, Object> parameters, List<Token> tokens) {
        // 检查SQL语法
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (Exception e) {
            throw new BadSqlGrammarException(Thread.currentThread().getName(), sql, new SQLException("SQL语法错误"));
        }
        String statementType = statement.getClass().getSimpleName();
        if (ObjectUtil.isNotEmpty(tokens) && tokens.stream().noneMatch(token -> statementType.toUpperCase().startsWith(token.name()))) {
            throw new BadSqlGrammarException(Thread.currentThread().getName(), sql, new SQLException("不支持的SQL类型"));
        }
        return sql;
    }

    @SuppressWarnings({"rawtypes"})
    public static Object parseResult(Token token, String targetSql, Object maps, ResultType resultType) {
        if (maps == null) {
            return null;
        }
        if (Arrays.stream(resultType.getSupportTokens()).noneMatch(e -> e.equals(token))) {
            throw new PersistenceException(token.name() + "不支持返回" + resultType.getReturnTypeDesc() + "类型");
        }
        if (resultType == ResultType.map) {
            if (maps instanceof List) {
                throw new TooManyResultsException("预期找到一条记录，但是找到了多条: " + ((List) maps).size());
            } else if (maps instanceof Map) {
                return maps;
            } else {
                throw new PersistenceException("预期返回一个" + resultType.getReturnTypeDesc() + "，但是实际类型为: " + maps.getClass().getSimpleName());
            }
        } else if (resultType == ResultType.listMap) {
            if (maps instanceof List) {
                return maps;
            } else {
                throw new PersistenceException("预期返回一个" + resultType.getReturnTypeDesc() + "，但是实际类型为: " + maps.getClass().getSimpleName());
            }
        } else if (resultType == ResultType.listStr) {// TODO: 2021/7/30
            return maps;
        } else if (resultType == ResultType.listNum) {// TODO: 2021/7/30
            return maps;
        } else if (resultType == ResultType.num) {// TODO: 2021/7/30
            return maps;
        } else if (resultType == ResultType.str) {// TODO: 2021/7/30
            return maps;
        } else if (resultType == ResultType.bool) {// TODO: 2021/7/30
            return maps;
        } else if (resultType == ResultType.page) {
            if (maps instanceof Page) {
                return maps;
            } else {
                throw new PersistenceException("预期返回一个" + resultType.getReturnTypeDesc() + "，但是实际类型为: " + maps.getClass().getSimpleName());
            }
        } else if (resultType == ResultType.pkValue) {// TODO: 2021/7/30
            return maps;
        }
        return maps;
    }

    /**
     * 解析SQL语句的类型
     *
     * @param sql 语句
     * @return 类型
     */
    public static Token getSqlToken(String sql) {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            log.error("SQL格式异常", e);
            throw new BusinessException("SQL格式异常");
        }
        Class<? extends Statement> statementClass = statement.getClass();
        Token token = Token.getByType(statementClass);
        if (Objects.isNull(token)) {
            throw new BusinessException("不支持的SQL类型: " + statementClass);
        }
        return token;
    }

}