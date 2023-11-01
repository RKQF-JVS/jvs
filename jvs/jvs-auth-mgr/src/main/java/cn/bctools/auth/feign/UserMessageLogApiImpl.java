package cn.bctools.auth.feign;

import cn.bctools.auth.api.api.UserMessageLogServiceApi;
import cn.bctools.auth.api.dto.UserMessageLogDto;
import cn.bctools.auth.entity.UserMessageLog;
import cn.bctools.auth.entity.enums.SendMessageTypeEnum;
import cn.bctools.auth.service.UserMessageLogService;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ZhuXiaoKang
 * @Description: 消息日志接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(tags = "[Feign]消息日志接口")
public class UserMessageLogApiImpl implements UserMessageLogServiceApi {

    private final UserMessageLogService userMessageLogService;

    @Override
    public R<Boolean> save(UserMessageLogDto userMessageLogDto) {
        UserMessageLog userMessageLog = BeanCopyUtil.copy(userMessageLogDto, UserMessageLog.class);
        userMessageLog.setSendMessageType(SendMessageTypeEnum.getCode(userMessageLogDto.getSendMessageType()));
        userMessageLogService.save(userMessageLog);
        return R.ok();
    }

    @Override
    public R<Boolean> saveBatch(List<UserMessageLogDto> userMessageLogDto) {
        List<UserMessageLog> userMessageLogs = userMessageLogDto.stream()
                .map(dto -> {
                    UserMessageLog userMessageLog = BeanCopyUtil.copy(dto, UserMessageLog.class);
                    userMessageLog.setSendMessageType(SendMessageTypeEnum.getCode(dto.getSendMessageType()));
                    return userMessageLog;
                }).collect(Collectors.toList());
        userMessageLogService.saveBatch(userMessageLogs);
        return R.ok();
    }
}
