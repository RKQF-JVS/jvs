package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.UserPermission;

/**
 * @author
 */
public interface UserPermissionService extends IService<UserPermission> {
//
//
//    /**
//     * 根据类型名称，ID，置换别名
//     * 根据类型，业务ID，用户ID，获取资源别名
//     *
//     * @param type         类型
//     * @param permissionId 权限表示
//     * @param userId       用户ID值
//     * @param defaultName  默认值，如果没有匹配到，直接返回默认值
//     * @return 如果没有找到默认返回defaultName 如果找到对应的中文名，则返回中文名字
//     */
//    String convertName(UserPermissionTypeEnum type, Integer permissionId, Integer userId, String defaultName);
//
//    /**
//     * 根据类型转换获取转换拿到用户自定义的名字
//     *
//     * @param type   菜单类型
//     * @param ids    数据集
//     * @param userId 用户ID
//     * @return 转换后的名字
//     */
//    List<String> convertName(UserPermissionTypeEnum type, List<Integer> ids, Integer userId);
}
