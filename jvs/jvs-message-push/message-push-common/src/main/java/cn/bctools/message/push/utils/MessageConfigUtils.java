//package cn.bctools.message.push.utils;
//
//import cn.bctools.auth.api.api.ApplyServiceApi;
//import cn.bctools.auth.api.dto.ApplyDto;
//import cn.bctools.message.push.entity.MessageConfig;
//import cn.bctools.message.push.entity.MessageConfigDetail;
//import cn.bctools.message.push.service.MessageConfigDetailService;
//import cn.bctools.message.push.service.MessageConfigService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Component
//@AllArgsConstructor
//public class MessageConfigUtils {
//
//    private final MessageConfigService messageConfigService;
//    private final MessageConfigDetailService messageConfigDetailService;
//    private final ApplyServiceApi applyServiceApi;
//
//    @Transactional(rollbackFor = Exception.class)
//    public void synchronizeClientData() {
//        List<ApplyDto> applyDtoList = applyServiceApi.all().getData();
//        List<MessageConfig> old = messageConfigService.list();
//        List<MessageConfig> messageConfigList  = applyDtoList.stream().map(e -> new MessageConfig()
//                .setId(e.getId())
//                .setClientCode(e.getAppKey())
//                .setClientName(e.getName())).collect(Collectors.toList());
//        messageConfigService.saveOrUpdateBatch(messageConfigList);
//
//        Map<String, ApplyDto> applyMap = applyDtoList.stream().collect(Collectors.toMap(ApplyDto::getId, Function.identity()));
//        //需要删除的配置
//        List<MessageConfig> delList = old.stream().filter(e -> !applyMap.containsKey(e.getId())).collect(Collectors.toList());
//        if(!delList.isEmpty()){
//            List<String> delIds = delList.stream().map(MessageConfig::getId).collect(Collectors.toList());
//            messageConfigService.removeByIds(delIds);
//            messageConfigDetailService.remove(new LambdaQueryWrapper<MessageConfigDetail>().in(MessageConfigDetail::getConfigId,delIds));
//        }
//    }
//}
