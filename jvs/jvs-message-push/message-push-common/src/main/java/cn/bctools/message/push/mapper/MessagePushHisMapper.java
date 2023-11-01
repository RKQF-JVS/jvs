package cn.bctools.message.push.mapper;

import cn.bctools.message.push.entity.MessagePushHis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 消息发送历史 Mapper 接口
 * </p>
 *
 * @author wl
 * @since 2022-05-18
 */
@Mapper
public interface MessagePushHisMapper extends BaseMapper<MessagePushHis> {

}
