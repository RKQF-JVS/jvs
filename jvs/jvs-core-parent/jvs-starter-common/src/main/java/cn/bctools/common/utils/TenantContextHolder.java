package cn.bctools.common.utils;

import cn.bctools.common.constant.SysConstant;
import lombok.experimental.UtilityClass;

/**
 * 当前线程租户处理工具
 *
 * @author My_gj
 */
@UtilityClass
public class TenantContextHolder {

    /**
     * TTL 设置当前线程租户ID
     *
     * @param tenantId 租户id
     */
    public void setTenantId(String tenantId) {
        SystemThreadLocal.set(SysConstant.TENANTID, tenantId);
    }

    /**
     * 获取当前线程租户ID
     *
     * @return 租户id
     */
    public String getTenantId() {
        return SystemThreadLocal.get(SysConstant.TENANTID);
    }

    public void clear() {
        SystemThreadLocal.remove(SysConstant.TENANTID);
    }

}
