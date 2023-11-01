package cn.bctools.message.push.service.impl;

import cn.bctools.message.push.dto.enums.MessagePushStatusEnum;
import cn.bctools.message.push.entity.MessagePushHis;
import cn.bctools.message.push.mapper.MessagePushHisMapper;
import cn.bctools.message.push.service.MessagePushHisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 消息发送历史 服务实现类
 * </p>
 *
 * @author wl
 * @since 2022-05-18
 */
@Service
@AllArgsConstructor
public class MessagePushHisServiceImpl extends ServiceImpl<MessagePushHisMapper, MessagePushHis> implements MessagePushHisService {

    @Override
    public List<MessagePushHis> getNoSuccessHisList(String batchNumber) {
        return this.list(new LambdaQueryWrapper<MessagePushHis>()
                .eq(MessagePushHis::getBatchNumber, batchNumber)
                .nested(e -> e.eq(MessagePushHis::getPushStatus, MessagePushStatusEnum.FAILED)
                        .or()
                        .eq(MessagePushHis::getPushStatus,MessagePushStatusEnum.WAIT))
        );
    }
}
