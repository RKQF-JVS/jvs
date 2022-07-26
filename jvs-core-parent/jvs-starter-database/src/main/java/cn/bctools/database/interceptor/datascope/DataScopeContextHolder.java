package cn.bctools.database.interceptor.datascope;

import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.utils.SystemThreadLocal;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * 数据权限拦截
 *
 * @author auto
 */
@UtilityClass
public class DataScopeContextHolder {

    private static final String KEY = "DATA_SCOPE";

    public void setDataScope(DataScopeDto datascope) {
        SystemThreadLocal.set(KEY, datascope);
    }

    /**
     * 获取数据权限配置项
     */
    public DataScopeDto getDataScope() {
        return Optional.ofNullable((DataScopeDto) SystemThreadLocal.get(KEY)).orElseGet(DataScopeDto::new);
    }

    public void clear() {
        SystemThreadLocal.remove(KEY);
    }

}
