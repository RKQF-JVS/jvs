package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.UserGroup;
import cn.bctools.auth.mapper.UserGroupMapper;
import cn.bctools.auth.service.UserGroupService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author auto
 */
@Service
@AllArgsConstructor
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements UserGroupService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUser(@NotNull String userId) {
        List<UserGroup> groups = this.list();
        List<UserGroup> updateGroups = new ArrayList<>();
        for (UserGroup group : groups) {
            List<String> users = group.getUsers();
            if (ObjectUtils.isNotEmpty(users) && users.contains(userId)) {
                users.removeIf(id -> id.equals(userId));
                updateGroups.add(group);
            }
        }
        this.updateBatchById(updateGroups);
    }

}
