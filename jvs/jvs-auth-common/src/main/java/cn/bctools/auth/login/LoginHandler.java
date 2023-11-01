package cn.bctools.auth.login;

import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.common.utils.SpringContextUtil;
import cn.bctools.oss.dto.BaseFile;
import cn.bctools.oss.template.OssTemplate;
import cn.hutool.http.HttpUtil;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

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

    /**
     * 绑定头像
     *
     * @param nickname 昵称
     * @param fileUrl  头像地址
     * @return
     */
    default String getDurableAvatar(String nickname, String fileUrl) {
        try {
            OssTemplate ossTemplate = SpringContextUtil.getBean(OssTemplate.class);
            byte[] bytes = HttpUtil.downloadBytes(fileUrl);
            BaseFile baseFile = ossTemplate.putFile("jvs-public", "/wx/avatar", nickname + ".jpg", new ByteArrayInputStream(bytes));
            return ossTemplate.fileLink(baseFile.getFileName(), "jvs-public");
        } catch (Exception e) {
            //微信头像下载失败
            return fileUrl;
        }
    }

    /**
     * 获取所有部门
     * @return
     */
    default List<Dept> getDeptAll() {
        return Collections.emptyList();
    }

    /***
     * 获取部门所有用户
     */
    default SyncUserDto getDeptUserAll(List<Dept> depts) {
        return null;
    }

}
