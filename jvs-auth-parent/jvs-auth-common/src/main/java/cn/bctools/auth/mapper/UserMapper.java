package cn.bctools.auth.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.bctools.auth.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过角色id集合获取用户数据
     *
     * @param roleIds 角色id集合
     * @return 用户数据集合
     */
    @Select("SELECT su.*,sur.role_id FROM sys_user su LEFT JOIN sys_user_role sur ON su.id = sur.user_id WHERE sur.role_id in ${roleIds} AND su.del_flag = 0")
    List<User> getByRoleIds(@Param("roleIds") String roleIds);

    /**
     * 通过部门名称获取用户
     *
     * @param deptName 部门全名称
     * @return
     */
    @Select("SELECT su.* FROM  sys_user su LEFT JOIN sys_dept sd ON su.dept_id = sd.id WHERE sd.`name` = '${deptName}'  and su.del_flag = 0")
    List<User> getQueryDeptName(@Param("deptName") String deptName);

    /**
     * 通过部门名称获取用户
     *
     * @param wrapper 部门全名称
     * @return 用户信息集合
     */
    @Select(" SELECT DISTINCT" +
            " su.*" +
            " FROM" +
            " sys_user su" +
            " LEFT JOIN sys_user_role sur ON sur.user_id = su.id" +
            " LEFT JOIN sys_role sr ON sr.id = sur.role_id and sr.del_flag = 0" +
            " where ${ew.sqlSegment} and su.del_flag = 0")
    List<User> getByQueryDto(@Param("ew") Wrapper<User> wrapper);

}