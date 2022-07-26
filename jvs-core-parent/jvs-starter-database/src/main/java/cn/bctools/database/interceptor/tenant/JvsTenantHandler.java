package cn.bctools.database.interceptor.tenant;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.getter.IDataSourceGetter;
import cn.bctools.database.getter.ITableFieldGetter;
import cn.bctools.database.util.TenantDynamicDatasourceUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author guojing
 */
@Slf4j
public class JvsTenantHandler {

    @Autowired
    private ITableFieldGetter tableFieldGetter;
    @Autowired
    private IDataSourceGetter dataSourceGetter;

    private static final String TENANT_ID = "tenant_id";

    /**
     * 获取租户 ID 值表达式，只支持单个 ID 值
     * <p>
     *
     * @return 租户 ID 值表达式
     */
    public Expression getTenantId() {
        String tenantId = TenantContextHolder.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            log.error("获取租户为空:请检查SQL数据>> ");
            return new NullValue();
        }
        return new StringValue(tenantId);
    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    public String getTenantIdColumn() {
        return TENANT_ID;
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     * <p>
     * 默认都要进行解析并拼接多租户条件
     *
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接多租户条件
     */
    public boolean ignoreTable(String tableName) {
        tableName = tableName.replace("`", "");
        String tenantId = TenantContextHolder.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            log.info("当前线程中没有租户id, 跳过租户拦截");
            return true;
        }
        // 校验是否有租户id字段
        DatabaseInfo databaseInfo = dataSourceGetter.getCurrent();
        List<String> tableFieldNames = tableFieldGetter.getFieldNames(databaseInfo.getIp(), databaseInfo.getPort(), databaseInfo.getDatabaseName(), tableName);
        if (ObjectUtils.isEmpty(tableFieldNames)) {
            log.info("数据表{}没有找到, 跳过租户拦截", tableName);
            return true;
        }
        if (!tableFieldNames.contains(TENANT_ID)) {
            log.info("数据表{}没有{}字段，跳过租户拦截", tableName, TENANT_ID);
            return true;
        }
        // 初始化租户数据库
        if(!TenantDynamicDatasourceUtil.init()) {
            return true;
        }

        return false;
    }

}