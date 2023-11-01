package cn.bctools.auth.service;

import cn.bctools.auth.login.dto.SyncUserDto;
import cn.bctools.common.entity.dto.UserDto;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserTenant;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户服务
 *
 * @author 
 */
public interface UserService extends IService<User> {

    /**
     * 保存一个用户，判断手机号是否唯一，并在租户表中添加数据
     *
     * @param user       用户信息
     * @param userTenant 用户租户信息
     * @return 保存之后的用户信息
     */
    User saveUser(User user, UserTenant userTenant);

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User info(String username);

    /**
     * 通过角色id集合获取用户信息
     *
     * @param roleIds 角色id集合
     * @return 用户信息集合
     */
    List<User> getByRoleIds(List<String> roleIds);

    /**
     * 通过部门名称获取用户
     *
     * @param deptName 部门名称
     * @return 用户信息集合
     */
    List<User> getQueryDeptName(String deptName);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User phone(String phone);

    /**
     * 修改用户信息
     *
     * @param getter 获取用户信息
     * @param value  用户信息值
     * @param userId 用户id
     * @param <T>    用户信息类型
     * @return 修改结果
     */
    <T> boolean updateInfo(SFunction<User, T> getter, T value, String userId);


    /**
     * 下载用户模板
     * @param response
     */
    void downloadExcelTemplate(HttpServletResponse response);

    /**
     * 导入用户
     * @param userDto 用户信息
     * @param file
     */
    void importUserExcel(UserDto userDto, MultipartFile file);

    /**
     * 拉取三方系统用户
     * @param syncUser
     */
    void pull(SyncUserDto syncUser);
}
