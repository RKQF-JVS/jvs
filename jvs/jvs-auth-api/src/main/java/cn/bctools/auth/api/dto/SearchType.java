package cn.bctools.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户搜索维度
 *
 * @author Administrator
 */
@Getter
@AllArgsConstructor
public enum SearchType {
    user, role, dept, job, group;
}
