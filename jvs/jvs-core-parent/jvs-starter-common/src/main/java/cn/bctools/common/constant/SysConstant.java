package cn.bctools.common.constant;

import cn.bctools.common.utils.SpringContextUtil;

/**
 * 系统常量
 * <p>
 * 包含请求头名称, RedisKey等
 *
 * @author Administrator
 */
public class SysConstant {

    /**
     * 版本号
     */
    public static final String JVS_AUTH = "jvsauth:";
    public static final String VERSION = "jvs-rule-ua";
    public static final String TENANTID = "jvs-tenantId";
    public static final String DEFAULT = "default";
    public static final String DATASCOPE = "datascope:applicationname:";
    /**
     * 应用名称, 详情见{@link SpringContextUtil#applicationContextName}
     */
    public static final String APPLICATION_NAME = "_application_name_";

    /**
     * 默认顶级id(如部门,菜单等)
     */
    public static final String ROOT_ID = "-1";

    /**
     * 用户邀请的唯一标识
     */
    public static final String INVITE = "tenantuserinvite";

    /**
     * 基础框架客户端基础关键字
     */
    public static final String FRAME = "frame";

    public static final String BASIC = "BASIC";
    public static final String DING_H5 = "DING_H5";
    public static final String WX_ENTERPRISE = "WX_ENTERPRISE";
    public static final String WECHAT_MP = "WECHAT_MP";
    public static final String LDAP = "LDAP";
    public static final String SMS = "SMS";
    public static final String WECHAT_MP_MESSAGE = "WECHAT_MP_MESSAGE";
    public static final String EMAIL = "EMAIL";

    /**
     * MySQL写锁关键词
     */
    public static final String FOR_UPDATE = "for update";

    /**
     * 统一规范化Redis的Key命名，保证所有的Key都可以便于管理
     *
     * @param module 功能模块简称
     * @param key    Redis的key
     * @return
     */
    public static synchronized String redisKey(String module, String key) {
        StringBuffer stringBuffer = new StringBuffer(SpringContextUtil.getApplicationContextName());
        stringBuffer.append(":");
        stringBuffer.append(module);
        stringBuffer.append(":");
        stringBuffer.append(key);
        return stringBuffer.toString().replaceAll("-", "");
    }

}
