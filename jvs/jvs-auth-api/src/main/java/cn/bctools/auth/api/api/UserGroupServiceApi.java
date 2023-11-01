package cn.bctools.auth.api.api;

import cn.bctools.auth.api.dto.UserGroupDto;
import cn.bctools.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户群组
 *
 * @Author: GuoZi
 */
@FeignClient(value = "jvs-auth-mgr", contextId = "userGroup")
public interface UserGroupServiceApi {

    String PREFIX = "/api/user/group";

    /**
     * 根据群组id查询群组信息
     *
     * @param groupId 群组id
     * @return 群组信息
     */
    @GetMapping(PREFIX + "/query/group/by/id/{groupId}")
    R<UserGroupDto> getById(@PathVariable("groupId") String groupId);

    /**
     * 根据群组id集合查询群组信息集合
     *
     * @param groupIds 群组id集合
     * @return 群组信息集合
     */
    @PostMapping(PREFIX + "/query/group/by/ids")
    R<List<UserGroupDto>> getByIds(@RequestBody List<String> groupIds);

}
