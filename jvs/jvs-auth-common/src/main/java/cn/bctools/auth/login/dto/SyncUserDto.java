package cn.bctools.auth.login.dto;

import cn.bctools.auth.entity.User;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description: 同步用户
 */
@Data
@Accessors(chain = true)
public class SyncUserDto {

    private List<User> users = Collections.synchronizedList(new ArrayList<>());
    private List<UserTenant> userTenants = Collections.synchronizedList(new ArrayList<>());
    private List<UserExtension> userExtensions = Collections.synchronizedList(new ArrayList<>());
}
