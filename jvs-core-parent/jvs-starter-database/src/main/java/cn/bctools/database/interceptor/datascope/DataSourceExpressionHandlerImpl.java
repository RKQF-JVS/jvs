package cn.bctools.database.interceptor.datascope;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import cn.bctools.common.entity.dto.DataDictDto;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.getter.IDataSourceGetter;
import cn.bctools.database.getter.ITableFieldGetter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 重写where条件达到数据权限处理的效果
 *
 * @author auto
 */
@Slf4j
public class DataSourceExpressionHandlerImpl implements IDataSourceExpressionHandler {

    @Autowired
    private ITableFieldGetter tableFieldGetter;
    @Autowired
    private IDataSourceGetter dataSourceGetter;

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
    public Expression apply(Expression currentExpression, Table table) {
        String tableName = table.getName().replace("`", "");
        // 获取当前数据源信息
        DatabaseInfo databaseInfo = dataSourceGetter.getCurrent();
        List<String> tableFieldNames = tableFieldGetter.getFieldNames(databaseInfo.getIp(), databaseInfo.getPort(), databaseInfo.getDatabaseName(), tableName);
        if (ObjectUtils.isEmpty(tableFieldNames)) {
            log.error(">>>> 没有找到加载的数据表{}字段信息,权限增强失败!", tableName);
            return currentExpression;
        }
        DataScopeDto dataScope = DataScopeContextHolder.getDataScope();
        List<String> jobIds = dataScope.getJobIds();
        List<String> deptIds = dataScope.getDeptIds();
        List<String> createByIds = dataScope.getCreateByIds();
        boolean hasJob = tableFieldNames.contains(JOB_ID);
        boolean hasDept = tableFieldNames.contains(DEPT_ID);
        boolean hasCreateById = tableFieldNames.contains(CREATE_BY_ID);
        // 基础权限
        currentExpression = getExpression(currentExpression, table, CREATE_BY_ID, hasCreateById, createByIds, false);
        currentExpression = getExpression(currentExpression, table, JOB_ID, hasJob, jobIds, false);
        currentExpression = getExpression(currentExpression, table, DEPT_ID, hasDept, deptIds, false);
        // 自定义权限
        List<DataDictDto> dataDictDto = dataScope.getDataDicts();
        if (ObjectUtils.isEmpty(databaseInfo)) {
            return currentExpression;
        }
        for (DataDictDto dictDto : dataDictDto) {
            // TODO 获取自定义权限值(单个值或者集合)
            String columnName = dictDto.getValue();
            String columnValue = null;
            boolean hasColumn = tableFieldNames.contains(columnName);
            currentExpression = getExpression(currentExpression, table, columnName, hasColumn, columnValue);
        }
        return currentExpression;
    }

    /**
     * 增强where条件
     *
     * @param currentExpression 当前where表达式
     * @param table             数据库表
     * @param columnName        数据权限字段名称
     * @param hasColumn         该表是否有数据权限字段
     * @param columnValue       数据权限值
     * @return 增强后的where表达式
     */
    private Expression getExpression(Expression currentExpression, Table table, String columnName, Boolean hasColumn, String columnValue) {
        if (StringUtils.isBlank(columnValue)) {
            return currentExpression;
        }
        return getExpression(currentExpression, table, columnName, hasColumn, Collections.singletonList(columnValue), true);
    }

    /**
     * 增强where条件
     *
     * @param currentExpression 当前where表达式
     * @param table             数据库表
     * @param columnName        数据权限字段名称
     * @param hasColumn         该表是否有数据权限字段
     * @param columnValues      数据权限范围
     * @param and               数据权限是否为交集
     * @return 增强后的where表达式
     */
    private Expression getExpression(Expression currentExpression, Table table, String columnName, Boolean hasColumn, Collection<String> columnValues, boolean and) {
        if (!hasColumn) {
            log.error(">>>> 数据表{}没有{}字段, 权限增强失败", table.getName(), columnName);
            return currentExpression;
        }
        if (ObjectUtils.isEmpty(columnValues)) {
            log.error(">>>> 数据权限为空: {}, 增强失败", columnName);
            return currentExpression;
        }
        if (and || columnValues.size() == 1) {
            // and A and B and C
            for (String columnValue : columnValues) {
                EqualsTo equalsTo = new EqualsTo();
                equalsTo.setLeftExpression(this.getAliasColumn(table, columnName));
                equalsTo.setRightExpression(new StringValue(columnValue));
                currentExpression = buildExpression(currentExpression, equalsTo);
            }
        } else {
            // and in (A,B,C)
            InExpression inExpression = new InExpression(getAliasColumn(table, columnName), new ExpressionList(columnValues.stream().distinct().map(StringValue::new).collect(Collectors.toList())));
            currentExpression = buildExpression(currentExpression, inExpression);
        }
        return currentExpression;
    }

    /**
     * 拼接where条件
     *
     * @param currentExpression 当前where表达式
     * @param expression        需要拼接的条件
     * @return where表达式
     */
    private static Expression buildExpression(Expression currentExpression, Expression expression) {
        if (currentExpression == null) {
            return expression;
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), expression);
        } else {
            return new AndExpression(currentExpression, expression);
        }
    }

    /**
     * 获取带别名的字段对象
     *
     * @param table 数据库表
     * @param field 字段名称
     * @return 字段对象
     */
    private Column getAliasColumn(Table table, String field) {
        StringBuilder column = new StringBuilder();
        Alias alias = table.getAlias();
        if (alias != null) {
            column.append(alias.getName()).append(StringPool.DOT);
        }
        column.append(field);
        return new Column(column.toString());
    }

}
