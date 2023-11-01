package cn.bctools.database.interceptor.datascope;

import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.utils.ObjectNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 字段数据权限， 不是租户数据权限。
 * 包含了创建人，部门岗位的数据权限操作 。和自定义注解字段的数据权限控制
 * 根据项目启动后扫描数据库是否有这几个字段，进行判断是否有数据权限，如果有才执行此数据权限功能
 *
 * @author 
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DataScopeInterceptor extends JsqlParserSupport implements InnerInterceptor {

    IDataSourceExpressionHandler dataSourceExpressionHandler;

    /**
     * 创建人
     */
    public static final String CREATE_BY_ID = "create_by_id";
    /**
     * 部门
     */
    public static final String DEPT_ID = "dept_id";
    /**
     * 岗位
     */
    public static final String JOB_ID = "job_id";

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
            return;
        }
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), ms.getId()));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreTenantLine(ms.getId())) {
                return;
            }
            if (SqlParserHelper.getSqlParserInfo(ms)) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(parserMulti(mpBs.sql(), null));
        }
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {

    }

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {

    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {

    }

    /**
     * 查询sql数据权限拦截处理
     * <p>
     * 针对所有查询语句做拦截，包括复杂嵌套的sql语句。
     * 会做数据权限的场景：
     * 第一，配置了数据权限{@link DataScopeContextHolder#setDataScope(DataScopeDto)}，其中这步不需要做，网关层会根据设置的数据权限做自动填充。
     * 第二，当前线程数据权限开启{@link DataScopeContextHolder#enable()}
     * 第三，如以下sql，
     * select id,a.name,type,create_time,a.update_time,deploy_version from crud_form a
     * join
     * (
     * select name,max(update_time) update_time
     * from crud_form
     * where name is not null and name !='' and type !='flowable' and flag =0
     * group by name
     * ) b on b.name=a.name and b.update_time=a.update_time
     * where a.name is not null and a.name !='' and a.type !='flowable' and a.flag =0 ORDER BY a.name,a.update_time desc;
     * 出现了内嵌查询，会拆分到每条查询语句对涉及到的所有表做数据权限处理，
     * 处理判断字段为{@link #CREATE_BY_ID}{@link #JOB_ID}{@link #DEPT_ID}
     * 如果当前子查询语句的数据表没有以上三个字段，则该表会跳过数据权限处理，如果配置了部门或岗位的数据权限，数据表没有{@link #JOB_ID}{@link #DEPT_ID}字段
     * 也可以做数据权限处理，前提是数据表用字段{@link #CREATE_BY_ID}搂底做用户层面的筛选。
     * </p>
     * <p>
     * 权限层级分为以下几种：
     * 所有部门
     * 所有岗位
     * 当前部门以及子部门
     * 当前岗位
     * 用户自己创建的数据
     * 自定义权限
     * </p>
     */
    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        DataScopeDto dataScope = DataScopeContextHolder.getDataScope();
        //判断表是否
        //远程调用服务被唤醒当前线程数据权限优先级最高 即使被调用服务关闭了数据权限,当前线程也会被唤醒
//        boolean enable = DataScopeContextHolder.isWakeUpRpcTtlDataScope() || (DataScopeContextHolder.isEnable() && BooleanUtil.isTrue(dataScopeIgnoreTable.getIs()));
//        if (enable && dataScope != null && (dataScope.getDataScopeType() != null || ObjectUtil.isNotEmpty(dataScope.getCustomColumnPermissions()))) {
//            Optional.ofNullable(dataScopeIgnoreTable.getIgnoreTables()).orElse(Collections.emptyList()).stream().filter(ObjectUtil::isNotEmpty).forEach(DataScopeContextHolder::addSkipTable);
//            Optional.ofNullable(dataScopeIgnoreTable.getInterceptTables()).orElse(Collections.emptyList()).stream().filter(ObjectUtil::isNotEmpty).forEach(DataScopeContextHolder::removeSkipTable);
//            List<String> skipTables = DataScopeContextHolder.getSkipTables();
//            log.debug(">>>> 数据权限拦截配置：{}，跳过数据权限的表：{}", dataScope, skipTables);
//            log.debug(">>>> 数据权限拦截，处理前sql：{}", select.toString());
//            this.processSelectBody(select.getSelectBody());
//            List<WithItem> withItemsList = select.getWithItemsList();
//            if (!CollectionUtils.isEmpty(withItemsList)) {
//                withItemsList.forEach(this::processSelectBody);
//            }
//            log.debug(">>>> 数据权限拦截，处理后sql：{}", select.toString());
//        }
        if (ObjectNull.isNotNull(dataScope.getDataScopeType())) {
            log.debug(">>>> 数据权限拦截，处理前sql：{}", select.toString());
            this.processSelectBody(select.getSelectBody());
            List<WithItem> withItemsList = select.getWithItemsList();
            if (!CollectionUtils.isEmpty(withItemsList)) {
                withItemsList.forEach(this::processSelectBody);
            }
            log.debug(">>>> 数据权限拦截，处理后sql：{}", select.toString());
        }
    }

    /**
     * 改写查询条件
     *
     * @param selectBody 查询语句
     */
    public void processSelectBody(SelectBody selectBody) {
        if (selectBody == null) {
            return;
        }
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            processSelectBody(withItem.getSelectBody());
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                operationList.getSelects().forEach(this::processSelectBody);
            }
        }
    }

    /**
     * 处理 PlainSelect
     */
    protected void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        Expression where = plainSelect.getWhere();
        processWhereSubSelect(where);
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            plainSelect.setWhere(builderExpression(where, fromTable));
        } else {
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            joins.forEach(j -> {
                processJoin(j);
                processFromItem(j.getRightItem());
            });
        }
    }

    /**
     * 处理where条件内的子查询
     * <p>
     * 支持如下:
     * 1. in
     * 2. =
     * 3. >
     * 4. <
     * 5. >=
     * 6. <=
     * 7. <>
     * 8. EXISTS
     * 9. NOT EXISTS
     * <p>
     * 前提条件:
     * 1. 子查询必须放在小括号中
     * 2. 子查询一般放在比较操作符的右边
     *
     * @param where where 条件
     */
    protected void processWhereSubSelect(Expression where) {
        if (where == null) {
            return;
        }
        if (where instanceof FromItem) {
            processFromItem((FromItem) where);
            return;
        }
        if (where.toString().indexOf("SELECT") > 0) {
            // 有子查询
            if (where instanceof BinaryExpression) {
                // 比较符号 , and , or , 等等
                BinaryExpression expression = (BinaryExpression) where;
                processWhereSubSelect(expression.getLeftExpression());
                processWhereSubSelect(expression.getRightExpression());
            } else if (where instanceof InExpression) {
                // in
                InExpression expression = (InExpression) where;
                ItemsList itemsList = expression.getRightItemsList();
                if (itemsList instanceof SubSelect) {
                    processSelectBody(((SubSelect) itemsList).getSelectBody());
                }
            } else if (where instanceof ExistsExpression) {
                // exists
                ExistsExpression expression = (ExistsExpression) where;
                processWhereSubSelect(expression.getRightExpression());
            } else if (where instanceof NotExpression) {
                // not exists
                NotExpression expression = (NotExpression) where;
                processWhereSubSelect(expression.getExpression());
            } else if (where instanceof Parenthesis) {
                Parenthesis expression = (Parenthesis) where;
                processWhereSubSelect(expression.getExpression());
            }
        }
    }

    /**
     * 处理子查询等
     */
    protected void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null) {
                subJoin.getJoinList().forEach(this::processJoin);
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            log.debug("Perform a subquery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }

    /**
     * 处理联接语句
     */
    protected void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            join.setOnExpression(builderExpression(join.getOnExpression(), fromTable));
        }
    }

    /**
     * 处理条件
     */
    protected Expression builderExpression(Expression currentExpression, Table table) {
        return dataSourceExpressionHandler.apply(currentExpression, table);
    }

}
