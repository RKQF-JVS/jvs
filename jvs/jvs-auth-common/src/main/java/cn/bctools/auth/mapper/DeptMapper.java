package cn.bctools.auth.mapper;

import cn.bctools.auth.entity.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 部门基础
 *
 * @author
 */
public interface DeptMapper extends BaseMapper<Dept> {

    @Update("UPDATE sys_dept SET del_flag = ${delFlag} WHERE id in ${deptIds} AND source = '${source}'")
    void updateDelFlag(@Param("delFlag") Boolean delFlag, @Param("source") String source, @Param("deptIds") String deptIds);
}
