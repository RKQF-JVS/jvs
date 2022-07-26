package cn.bctools.database.interceptor.datascope;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

/**
 * 重写where条件达到数据权限处理的效果
 *
 * @author auto
 */
public interface IDataSourceExpressionHandler {

    /**
     * 重写where条件达到数据权限处理的效果
     *
     * @param currentExpression 原where条件
     * @param table             操作的表
     * @return 处理后的where条件
     */
    Expression apply(Expression currentExpression, Table table);

}
