package cn.bctools.message.push.utils;

import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.TenantContextHolder;
import cn.bctools.message.push.dto.messagePush.ReceiversDto;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class JvsUserComponent {
    private final AuthUserServiceApi authUserServiceApi;

    public static final String DEFAULT_TENANT_ID = "-1";
    public static final String DEFAULT_ID = "-1";

    public static final String DEFAULT_HDEAIMG = "http://jvsoss.bctools.cn/jvs-public/wx/avatar/2022-01-18-668103781807722496-520QT.jpg";

    public UserDto getCurrentUser(){
        UserDto userDto = new UserDto().setId(DEFAULT_ID).setRealName("系统通知").setHeadImg(DEFAULT_HDEAIMG);
        try {
            UserDto currentUser = UserCurrentUtils.getCurrentUser();
            return Optional.ofNullable(currentUser).isPresent()?currentUser:userDto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    public UserDto getCurrentUser(String clientCode){
        UserDto userDto = new UserDto().setTenantId(DEFAULT_TENANT_ID).setId(DEFAULT_ID).setRealName(Optional.ofNullable(clientCode).orElse("系统消息")).setHeadImg(DEFAULT_HDEAIMG);
        try {
            UserDto currentUser = UserCurrentUtils.getCurrentUser();
            return Optional.ofNullable(currentUser).isPresent()?currentUser:userDto;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return userDto;
    }

    public List<UserDto> findByIds(List<ReceiversDto> list) {
        List<String> userIds = list.stream().map(ReceiversDto::getUserId).distinct().collect(Collectors.toList());
        return authUserServiceApi.getByIds(userIds).getData();
    }

    /**
     * 站内信接收人配置
     * @param receivers 接收人
     */
    public void setInsideNoticeConfig(List<ReceiversDto> receivers){
        List<UserDto> userDtoList = this.findByIds(receivers);
        this.setTenantId(userDtoList);
        Map<String, UserDto> userDtoMap = userDtoList.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
        receivers.forEach(e -> {
            UserDto userDto = userDtoMap.get(e.getUserId());
            e.setUserName(userDto.getRealName()).setReceiverConfig(userDto.getId());
        });
    }

    /**
     * 设置微信公众号 配置
     * @param receivers 接收人
     */
    public void setWxMpConfig(List<ReceiversDto> receivers){
        List<UserDto> userDtoList = this.findByIds(receivers);
        this.setTenantId(userDtoList);
        Map<String, UserDto> userDtoMap = userDtoList.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
        receivers.forEach(e -> {
            UserDto userDto = userDtoMap.get(e.getUserId());
            e.setUserName(userDto.getRealName());
            if(ObjectNull.isNotNull(userDto.getExceptions()) && userDto.getExceptions().containsKey("WECHAT_MP")){
                JSONObject wechat_mp = JSONUtil.parseObj(userDto.getExceptions().get("WECHAT_MP"));
                e.setReceiverConfig(wechat_mp.getStr("openId"));
            }
            log.info("微信公众号：模板：用户：{} ,属性：{},转换后: {}",userDto.getRealName(),userDto.getExceptions(),e.toString());
        });
    }

    /**
     * 设置邮件 配置
     * @param receivers 接收人
     */
    public void setEmailConfig(List<ReceiversDto> receivers){
        List<UserDto> userDtoList = this.findByIds(receivers);
        this.setTenantId(userDtoList);
        Map<String, UserDto> userDtoMap = userDtoList.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
        receivers.forEach(e -> {
            UserDto userDto = userDtoMap.get(e.getUserId());
            e.setUserName(userDto.getRealName()).setReceiverConfig(userDto.getEmail());

        });
    }

    /**
     * 设置短信 配置
     * @param receivers 接收人
     */
    public void setAliSmsConfig(List<ReceiversDto> receivers){
        List<UserDto> userDtoList = this.findByIds(receivers);
        this.setTenantId(userDtoList);
        Map<String, UserDto> userDtoMap = userDtoList.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
        receivers.forEach(e -> {
            UserDto userDto = userDtoMap.get(e.getUserId());
            e.setUserName(userDto.getRealName()).setReceiverConfig(userDto.getPhone());
        });
    }

    /**
     * 设置租户
     * @param userDtoList 查询的接收人数据
     */
    public void setTenantId(List<UserDto> userDtoList){
        Optional<String> first = userDtoList.stream().map(UserDto::getTenantId).findFirst();
        if(first.isPresent()){
            TenantContextHolder.setTenantId(first.get());
        }
    }
}
