package cn.bctools.auth.login;

import cn.bctools.auth.entity.User;

/**
 * @author Administrator
 */
public interface LoginHandler<T> {

    /**
     * 根据code码获取用户信息
     *
     * @param code  登录参数
     * @param appId 应用ID 主要用户特殊登录的数据加密操作,和判定是否支持此应用登录
     * @param t     用户登录参数
     * @return 用户信息
     */
    User handle(String code, String appId, T t);

    /**
     * 绑定逻辑
     *
     * @param user  用户对象，是哪一个用户需要绑定
     * @param code  三方code码
     * @param appId 应用ID哪一个前端项目在绑定
     */
    void bind(User user, String code, String appId);

}
