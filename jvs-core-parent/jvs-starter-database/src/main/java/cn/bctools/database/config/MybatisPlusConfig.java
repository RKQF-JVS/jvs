package cn.bctools.database.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.database.entity.DatabaseInfo;
import cn.bctools.database.getter.DefaultTableFieldGetter;
import cn.bctools.database.interceptor.datascope.DataScopeInterceptor;
import cn.bctools.database.interceptor.datascope.DataSourceExpressionHandlerImpl;
import cn.bctools.database.interceptor.datascope.IDataSourceExpressionHandler;
import cn.bctools.database.interceptor.other.CustomOthersInterceptor;
import cn.bctools.database.interceptor.performance.SQLPerformanceInterceptor;
import cn.bctools.database.interceptor.tenant.JvsTenantHandler;
import cn.bctools.database.interceptor.tenant.JvsTenantLineInnerInterceptor;
import cn.bctools.database.property.SqlProperties;
import cn.bctools.database.util.DatabaseUtils;
import cn.bctools.database.util.TenantDynamicDatasourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author My_gj
 */
@Slf4j
@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@MapperScan(basePackages = {"cn.bctools.**.mapper"})
public class MybatisPlusConfig {

    @Bean
    @ConditionalOnMissingBean
    public IDataSourceExpressionHandler dataSourceExpressionHandler() {
        return new DataSourceExpressionHandlerImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataScopeInterceptor dataScopeInterceptor() {
        DataScopeInterceptor dataScopeInterceptor = new DataScopeInterceptor();
        dataScopeInterceptor.setDataSourceExpressionHandler(dataSourceExpressionHandler());
        return dataScopeInterceptor;
    }

    @Autowired
    void tenantInit(NacosDiscoveryProperties nacosDiscoveryProperties) {
        //是否开启Mycat租户数据隔离
        nacosDiscoveryProperties.getMetadata().put("tenantDynamicDatasource", "open");
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor() {
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor() {

            @Override
            protected String changeTable(String sql) {
                TableNameParser parser = new TableNameParser(sql);
                List<TableNameParser.SqlToken> names = new ArrayList<>();
                parser.accept(names::add);
                StringBuilder builder = new StringBuilder();
                int last = 0;
                for (TableNameParser.SqlToken name : names) {
                    int start = name.getStart();
                    if (start != last) {
                        builder.append(sql, last, start);
                        String value = name.getValue();
                        if (DefaultTableFieldGetter.getTableList().contains(value)) {
                            builder.append(dynamicTableName(sql, value));
                        } else {
                            builder.append(value);
                        }
                    }
                    last = name.getEnd();
                }
                if (last != sql.length()) {
                    builder.append(sql.substring(last));
                }
                return builder.toString();
            }

            private String dynamicTableName(String sql, String tableName) {
                String tenantId = TenantContextHolder.getTenantId();
                if (StrUtil.isBlank(tenantId)) {
                    return tableName;
                }
                SqlProperties sqlProperties = SpringContextUtil.getBean(SqlProperties.class);
                if (!sqlProperties.isDynamicTenantDatabase()) {
                    return tableName;
                }
                try {
                    DataSource dataSource = SpringContextUtil.getBean(DataSource.class);
                    String url = dataSource.getConnection().getMetaData().getURL();
                    DatabaseInfo info = DatabaseUtils.parseUrl(url);
                    tableName = info.getDatabaseName() + TenantDynamicDatasourceUtil.NAME_JOINER + tenantId + "." + tableName;
                } catch (SQLException throwables) {
                    log.error("SQL初始化错误", throwables);
                }
                return tableName;
            }
        };
        return dynamicTableNameInnerInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public JvsTenantLineInnerInterceptor jvsTenantLineInnerInterceptor(JvsTenantHandler jvsTenantHandler) {
        return new JvsTenantLineInnerInterceptor(jvsTenantHandler);
    }

    /**
     * 预留插件扩展
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CustomOthersInterceptor otherInterceptor(DataScopeInterceptor dataScope,
                                                    JvsTenantLineInnerInterceptor jvsTenant,
                                                    DynamicTableNameInnerInterceptor table) {
        return new CustomOthersInterceptor() {
            @Override
            public List<InnerInterceptor> gets() {
                return init(dataScope, jvsTenant, table, new BlockAttackInnerInterceptor(), new PaginationInnerInterceptor(DbType.MYSQL));
            }
        };
    }

    /**
     * 租户管理
     */
    @Bean
    @ConditionalOnMissingBean
    public JvsTenantHandler tenantHandler() {
        return new JvsTenantHandler();
    }

    /**
     * 性能分析
     */
    @Bean
    @ConditionalOnMissingBean
    public SQLPerformanceInterceptor sqlInterceptorConfig() {
        return new SQLPerformanceInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(CustomOthersInterceptor customOthersInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加自定义插件扩展预留
        List<InnerInterceptor> gets = customOthersInterceptor.gets();
        if (ObjectUtil.isNotEmpty(gets)) {
            for (InnerInterceptor get : gets) {
                interceptor.addInnerInterceptor(get);
            }
        }
        return interceptor;
    }

}
