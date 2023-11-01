package cn.bctools.message.push.service;

import cn.bctools.message.push.entity.MessagePushHis;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 消息发送历史 服务类
 * </p>
 *
 * @author wl
 * @since 2022-05-18
 */
public interface MessagePushHisService extends IService<MessagePushHis> {

    /**
     * 查询等待中或失败的消息历史记录
     * @param batchNumber 消息批次号
     * @return 消息历史
     */
    List<MessagePushHis> getNoSuccessHisList(String batchNumber);

}
