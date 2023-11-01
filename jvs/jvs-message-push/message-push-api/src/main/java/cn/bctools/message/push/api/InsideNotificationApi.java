package cn.bctools.message.push.api;

import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.InsideNoticeDto;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内
 */
@FeignClient(value = "message-push-mgr",contextId = "inside-notification")
public interface InsideNotificationApi {

    String prefix = "/inside/notification";

    /**
     * 站内信
     * @param messageDto 消息数据
     * @return 执行状态
     */
    @PostMapping(prefix+"/inside")
    R<Boolean> send(@RequestBody InsideNotificationDto messageDto);

    /**
     * 重新发送
     * @param pushHisId 消息数据
     * @return 执行状态
     */
    @GetMapping(prefix+"/inside/resend")
    R<Boolean> resend(String pushHisId);

    /**
     * 分页查询消息
     * @param current 当前页数
     * @param size 页 大小
     * @param dto 查询条件
     * @return 分页数据
     */
    @PostMapping(prefix+"/page")
    R<Page<InsideNoticeDto>> page(@RequestParam("current")Long current, @RequestParam("size")Long size, @RequestBody InsideNoticeDto dto);

    /**
     * 查询消息
     * @param dto 查询条件
     * @return 分页数据
     */
    @PostMapping(prefix+"/list")
    R<List<InsideNoticeDto>> list(@RequestBody InsideNoticeDto dto);

    /**
     * 查询详细
     * @param id 消息通知id
     * @return 消息通知详情
     */
    @GetMapping(prefix+"/{id}/detail")
    R<InsideNoticeDto> detail(@PathVariable("id") String id);

    /**
     * 设置消息通知已读
     * @param id 消息通知id
     * @return 消息通知详情
     */
    @GetMapping(prefix+"/{id}/read")
    R<Boolean> readIs(@PathVariable("id") String id);

    /**
     * 设置所有消息通知已读
     * @return 消息通知详情
     */
    @GetMapping(prefix+"/read")
    R<Boolean> allReadIs();
}
