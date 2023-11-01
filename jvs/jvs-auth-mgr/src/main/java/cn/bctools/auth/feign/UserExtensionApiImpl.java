package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.UserExtensionServiceApi;
import cn.bctools.auth.api.dto.UserExtensionDto;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "[Feign]用户扩展信息表")
public class UserExtensionApiImpl implements UserExtensionServiceApi {

    private final UserExtensionService userExtensionService;

    @Override
    public R<List<UserExtensionDto>> query(List<String> userIds, String type) {
        List<UserExtension>  list = userExtensionService.list(new LambdaQueryWrapper<UserExtension>()
                        .in(UserExtension::getUserId, userIds).eq(UserExtension::getType, type));
        if (CollectionUtils.isEmpty(list)) {
            return R.ok();
        }
        List<UserExtensionDto> userExtensions = BeanCopyUtil.copys(list, UserExtensionDto.class);
        return R.ok(userExtensions);
    }
}
