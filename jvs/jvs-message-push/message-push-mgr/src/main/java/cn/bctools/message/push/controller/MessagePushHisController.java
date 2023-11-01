package cn.bctools.message.push.controller;

import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.message.push.dto.messagePush.MessagePushHisDto;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.service.MessagePushHisService;
import cn.bctools.message.push.service.MessageResendService;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 消息发送历史 前端控制器
 * </p>
 *
 * @author wl
 * @since 2022-05-18
 */
@RestController
@RequestMapping("/message/push/his")
@Api(tags = "消息发送历史")
@AllArgsConstructor
public class MessagePushHisController {

    private final MessagePushHisService messagePushHisService;
    private final MessageResendService messageResendService;

    @GetMapping
    @ApiOperation("查询所有发送历史")
    public R<List<MessagePushHis>> list(MessagePushHisDto dto){
        LambdaQueryWrapper<MessagePushHis> qeuryWrapper = this.createQeuryWrapper(dto);
        return R.ok(messagePushHisService.list(qeuryWrapper));
    }

    @GetMapping("/page")
    @ApiOperation("分页查询发送历史")
    public R<Page<MessagePushHis>> page(Page<MessagePushHis> page,MessagePushHisDto dto){
        LambdaQueryWrapper<MessagePushHis> qeuryWrapper = this.createQeuryWrapper(dto);
        return R.ok(messagePushHisService.page(page,qeuryWrapper));
    }

    @GetMapping("/{id}/resend")
    @ApiOperation("重新发送消息")
    public R<Boolean> resend(@ApiParam("消息发送历史id")@PathVariable("id")String hisId){
        messageResendService.resendMessage(hisId);
        return R.ok();
    }

    private LambdaQueryWrapper<MessagePushHis> createQeuryWrapper(MessagePushHisDto dto){
        return new LambdaQueryWrapper<MessagePushHis>()
                .eq(StringUtil.isNotBlank(dto.getBatchNumber()),MessagePushHis::getBatchNumber,dto.getBatchNumber())
                .eq(ObjectUtil.isNotNull(dto.getPlatform()),MessagePushHis::getPlatform,dto.getPlatform())
                .eq(ObjectUtil.isNotNull(dto.getMessageType()),MessagePushHis::getMessageType,dto.getMessageType())
                .eq(ObjectUtil.isNotNull(dto.getPushStatus()),MessagePushHis::getPushStatus,dto.getPushStatus())
                .eq(StringUtil.isNotBlank(dto.getClientCode()),MessagePushHis::getClientCode,dto.getClientCode())
                .eq(StringUtil.isNotBlank(dto.getUserId()),MessagePushHis::getUserId,dto.getUserId())
                .between(ObjectNull.isNotNull(dto.getQueryStartTime())&&ObjectNull.isNotNull(dto.getQueryEndTime()),MessagePushHis::getCreateTime,dto.getQueryStartTime(),dto.getQueryEndTime())
                .orderByDesc(MessagePushHis::getCreateTime);
    }

}
