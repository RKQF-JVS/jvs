package cn.bctools.auth.mapper;

import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.entity.po.TenantUserData;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author
 */
public interface UserTenantMapper extends BaseMapper<UserTenant> {

    /**
     * 租户和用户关联表别名
     */
    String SYS_USER_TENANT_ALIAS = "sysUserTenant";
    /**
     * 用户表别名
     */
    String SYS_USER_ALIAS = "sysUser";

    /**
     * 分页查询租户下的用户
     * @param page 分页条件
     * @param wrapper 查询条件
     * @return
     */
    @Select(" SELECT " +
            " sysUserTenant.*, " +
            " sysUser.email, sysUser.sex, sysUser.account_name, sysUser.head_img, sysUser.invite, sysUser.user_type, sysUser.birthday  " +
            " FROM sys_user_tenant sysUserTenant LEFT JOIN sys_user sysUser ON sysUserTenant.user_id = sysUser.id " +
            " ${ew.customSqlSegment} " +
            " ORDER BY sysUserTenant.create_time desc")
    IPage<TenantUserData> tenantUsers(Page page, @Param("ew") Wrapper wrapper);
}
