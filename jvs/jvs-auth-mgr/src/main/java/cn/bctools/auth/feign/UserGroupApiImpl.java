package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.UserGroupServiceApi;
import cn.bctools.auth.api.dto.UserGroupDto;
import cn.bctools.auth.entity.UserGroup;
import cn.bctools.auth.service.UserGroupService;
import cn.bctools.common.utils.DozerUtil;
import cn.bctools.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户群组
 *
 * @Author: GuoZi
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "[Feign]用户群组接口")
public class UserGroupApiImpl implements UserGroupServiceApi {

    private final UserGroupService groupService;

    @Override
    public R<UserGroupDto> getById(String groupId) {
        UserGroup group = groupService.getById(groupId);
        if (Objects.isNull(group)) {
            return R.ok(null, "群组不存在");
        }
        return R.ok(DozerUtil.map(group, UserGroupDto.class));
    }

    @Override
    public R<List<UserGroupDto>> getByIds(List<String> groupIds) {
        if (ObjectUtils.isEmpty(groupIds)) {
            return R.ok(Collections.emptyList());
        }
        List<UserGroup> groups = groupService.listByIds(groupIds);
        if (ObjectUtils.isEmpty(groups)) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(DozerUtil.mapList(groups, UserGroupDto.class));
    }

}
