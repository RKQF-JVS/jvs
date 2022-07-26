package cn.bctools.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限分类
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {

    /**
     * 所有部门
     */
    all_dept,
    /**
     * 当前部门
     */
    curr_dept,
    /**
     * 当前部门以及子部门
     */
    curr_dept_tree,
    /**
     * 所有岗位
     */
    all_job,
    /**
     * 当前岗位
     */
    curr_job,
    /**
     * 用户自己创建的数据
     */
    self,
    /**
     * 自定义数据权限
     */
    customize;

}
