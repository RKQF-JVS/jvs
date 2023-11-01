package cn.bctools.auth.util;

/**
 * @Author: ZhuXiaoKang
 * @Description: 同步组织架构工具
 */
public class SyncOrgUtils {

    /**
     * 部门主键id分隔符
     */
    private static final String DEPT_ID_JOIN = "_";

    private SyncOrgUtils() {

    }

    /**
     * 同步三方平台部门，构造本地部门主键id
     * @param tenantId 租户id
     * @param otherDeptId 三方系统部门id
     * @return 本地部门主键id
     */
    public static String buildDeptId(String tenantId, String otherDeptId) {
        // 三方平台同步的id规则为：租户id_三方部门id
        return tenantId + DEPT_ID_JOIN + otherDeptId;
    }
}
