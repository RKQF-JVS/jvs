package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.Message;
import cn.bctools.auth.entity.enums.MessageStatusEnum;
import cn.bctools.auth.mapper.MessageMapper;
import cn.bctools.auth.service.MessageService;
import cn.bctools.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {


    @Override
    public Message sendById(String id) {
        // 查询消息
        Message message = this.getById(id);
        if (null == message) {
            throw new BusinessException("该消息不存在(id:" + id + ")");
        }
        message.setStatus(MessageStatusEnum.SENDING.getCode());
        this.updateById(message);
        return message;
    }

}
