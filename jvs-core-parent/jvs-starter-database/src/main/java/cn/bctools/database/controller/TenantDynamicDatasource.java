package cn.bctools.database.controller;

import cn.bctools.common.utils.R;
import cn.bctools.database.getter.IDataSourceGetter;
import cn.bctools.database.getter.ITableFieldGetter;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

/**
 * 多租户数据自动隔离，基于Mycat动态处理
 *
 * @since: 1.0.0
 * @author: auto
 */
@Slf4j
@RequestMapping("/api/tenantdynamic/datasource/tenant")
@RestController
@AllArgsConstructor
@Api(tags = "多租户动态回调")
public class TenantDynamicDatasource {

    static String sql = "CREATE DATABASE %s";
    IDataSourceGetter dataSourceGetter;
    ITableFieldGetter tableFieldGetter;
    DataSource dataSource;

    @SneakyThrows
    @GetMapping("/{tenantId}")
    public R index(@PathVariable("tenantId") String tenantId) {
//        // 校验是否有租户id字段
//        if (DataSourceInit.getIsTenantId()) {
//            try {
//                String url = dataSource.getConnection().getMetaData().getURL();
//                DatabaseInfo databaseInfo = DatabaseUtils.parseUrl(url);
//                //数据库名
//                String databaseName = databaseInfo.getDatabaseName() + NAME_JOINER + tenantId;
//                String format = String.format(sql, databaseName);
//                //真实 创建数据库
//                dataSource.getConnection().createStatement().execute(format);
//            } catch (SQLException throwables) {
//                log.error("创建租户数据源失败", throwables);
//            }
//        }
        return R.ok();
    }
}
