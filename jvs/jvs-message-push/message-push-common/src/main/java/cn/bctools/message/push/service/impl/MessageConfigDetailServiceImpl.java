//package cn.bctools.message.push.service.impl;
//
//import cn.bctools.common.exception.BusinessException;
//import cn.bctools.message.push.dto.enums.PlatformEnum;
//import cn.bctools.message.push.entity.MessageConfigDetail;
//import cn.bctools.message.push.mapper.MessageConfigDetailMapper;
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
///**
// * <p>
// * 配置详情 服务实现类
// * </p>
// *
// * @author wl
// * @since 2022-05-18
// */
//@Service
//@AllArgsConstructor
//public class MessageConfigDetailServiceImpl extends ServiceImpl<MessageConfigDetailMapper, MessageConfigDetail> implements MessageConfigDetailService {
//
//    @Override
//    public MessageConfigDetail findByCode(String code, PlatformEnum platformEnum) {
//        //查询客户端配置
//        MessageConfigDetail clientDetail = this.getOne(new LambdaQueryWrapper<MessageConfigDetail>()
//                .eq(MessageConfigDetail::getClientCode, code)
//                .eq(MessageConfigDetail::getPlatform, platformEnum));
//        if(clientDetail==null){
//            throw new BusinessException("当前客户端未配置"+platformEnum.getDesc());
//        }
//        return clientDetail;
//    }
//}
