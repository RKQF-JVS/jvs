package cn.bctools.message.push.service;

import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.message.push.dto.messagePush.AliSmsDto;
import cn.bctools.message.push.dto.vo.AliSmsTemplateVo;

import java.util.List;

public interface AliSmsService {

    /**
     * 发送短信
     *
     * @param dto      短信数据
     * @param pushUser 消息发送人
     */
    void send(AliSmsDto dto, UserDto pushUser);

    /**
     * 重新发送短信
     *
     * @param pushHisId 发送历史
     */
    void resend(String pushHisId);

    /**
     * 查询所有模板
     *
     * @param clientCode 终端唯一标识
     * @param pageIndex  页码
     * @param pageSize   总数
     * @return
     */
    List<AliSmsTemplateVo> getAllPrivateTemplate(Integer pageIndex, Integer pageSize);
}
