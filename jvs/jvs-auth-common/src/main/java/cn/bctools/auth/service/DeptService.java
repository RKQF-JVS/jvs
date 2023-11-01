package cn.bctools.auth.service;

import cn.bctools.auth.entity.Dept;
import cn.bctools.common.entity.dto.UserDto;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author
 */
public interface DeptService extends IService<Dept> {

    /**
     * 移除指定用户信息
     * <p>
     * 仅处理部门负责人信息
     *
     * @param userId 用户id
     */
    void clearUser(@NotNull String userId);

    /**
     * 校验部门id
     *
     * @param deptId 部门id
     * @return 部门信息
     */
    Dept checkId(String deptId);

    /**
     * 获取指定部门的所有子部门id(不包括当前部门)
     *
     * @param deptId 部门id
     * @return 子部门id集合
     */
    List<String> getAllChildId(String deptId);

    /**
     * 拉取三方系统组织架构
     *
     * @param userDto
     * @param remoteDepts 三方系统组织架构
     */
    void pull(UserDto userDto, List<Dept> remoteDepts);

}
