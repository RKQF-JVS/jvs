package cn.bctools.oauth2.utils;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * 获取当前用户
 *
 * @author guojing
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
public class UserCurrentUtils {

    public static final String USER = "user";
    public static final String UN_LOGIN = "anonymousUser";

    /**
     * 通过上下文获取用户信息
     *
     * @return 用户信息
     */
    public synchronized static UserInfoDto<? extends UserDto> init() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (UN_LOGIN.equals(principal)) {
            throw new BusinessException("用户未登录");
        }
        return (UserInfoDto<? extends UserDto>) principal;
    }

    /**
     * 获取当前登录用户,由于不是使用的Tomcat，所以每次通过请求头进行获取即可
     *
     * @return 用户信息
     */
    public static UserDto getCurrentUser() {
        return init().getUserDto();
    }

    /**
     * 获取当前登录用户, 未登录时返回null
     *
     * @return 用户信息
     */
    public static UserDto getNullableUser() {
        try {
            return getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户真名
     *
     * @return 用户真名
     */
    public static String getRealName() {
        return getCurrentUser().getRealName();
    }

    /**
     * 获取登录名
     *
     * @return 登录名
     */
    public static String getAccountName() {
        return getCurrentUser().getAccountName();
    }

    /**
     * 获取角色ID集
     *
     * @return 角色ID集
     */
    public static List<String> getRole() {
        return init().getRoles();
    }

    /**
     * 获取用户所在部门ID
     *
     * @return 部门ID
     */
    public static String getDeptId() {
        return getCurrentUser().getDeptId();
    }

    /**
     * 获取用户所在部门名称
     *
     * @return 部门名称
     */
    public static String getDeptName() {
        return getCurrentUser().getDeptName();
    }

    /**
     * 获取用户ID，先通过请求头解析用户对象，然后拿用户对象获取用户ID值
     *
     * @return java.lang.Integer
     **/
    public static String getUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 获取当前用户账号等级
     *
     * @return 账号等级
     */
    public static Integer getLevel() {
        return getCurrentUser().getLevel();
    }

}
