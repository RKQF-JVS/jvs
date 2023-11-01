package cn.bctools.message.push.feign;

import cn.bctools.auth.api.api.ApplyServiceApi;
import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.auth.api.dto.ApplyDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.ObjectNull;
import cn.bctools.common.utils.R;
import cn.bctools.message.push.api.InsideNotificationApi;
import cn.bctools.message.push.dto.messagePush.InsideNoticeDto;
import cn.bctools.message.push.dto.messagePush.InsideNotificationDto;
import cn.bctools.message.push.entity.InsideNotice;
import cn.bctools.message.push.service.InsideNoticeService;
import cn.bctools.message.push.service.InsideNotificationService;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestMapping
@RestController
@AllArgsConstructor
@Api(tags = "[Feign] 站内信接口")
public class InsideNotificationApiImpl implements InsideNotificationApi {

    private final InsideNotificationService insideNotificationService;

    private final InsideNoticeService insideNoticeService;
    private final AuthUserServiceApi authUserServiceApi;
    private final ApplyServiceApi applyServiceApi;

//    private final UserComponent userComponent;

    @Override
    public R<Boolean> send(InsideNotificationDto messageDto) {
        insideNotificationService.send(messageDto, UserCurrentUtils.getCurrentUser());
        return R.ok(Boolean.TRUE);
    }

    @Override
    public R<Boolean> resend(String pushHisId) {
        insideNotificationService.resend(pushHisId);
        return R.ok(Boolean.TRUE);
    }

    @Override
    public R<Page<InsideNoticeDto>> page(Long current,Long size, InsideNoticeDto dto) {
        Page<InsideNotice> queryPage = new Page<>();
        queryPage.setCurrent(current).setSize(size);
        insideNoticeService.page(queryPage,createQueryWrapper(dto));

        Page<InsideNoticeDto> page = new Page<>();
        BeanUtil.copyProperties(queryPage,page);

        Map<String, ApplyDto> clientMap = applyServiceApi.all().getData().stream().collect(Collectors.toMap(ApplyDto::getAppKey, Function.identity()));

        page.setRecords(queryPage.getRecords().stream().map(e -> {
            InsideNoticeDto insideNoticeDto = convert2Dto(e);
            insideNoticeDto.setClientName(clientMap.get(insideNoticeDto.getClientCode()).getName());
            return insideNoticeDto;
        }).collect(Collectors.toList()));

        return R.ok(page);
    }

    @Override
    public R<List<InsideNoticeDto>> list(InsideNoticeDto dto) {

        List<InsideNotice> list = insideNoticeService.list(createQueryWrapper(dto));

        Map<String, ApplyDto> clientMap = applyServiceApi.all().getData().stream().collect(Collectors.toMap(ApplyDto::getAppKey, Function.identity()));

        List<InsideNoticeDto> result = list.stream().map(e -> {
            InsideNoticeDto insideNoticeDto = convert2Dto(e);
            insideNoticeDto.setClientName(clientMap.get(insideNoticeDto.getClientCode()).getName());
            return insideNoticeDto;
        }).collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    public R<InsideNoticeDto> detail(String id) {
        InsideNotice notice = insideNoticeService.getById(id);
        if(ObjectNull.isNull(notice)){
            throw new BusinessException("未找到该站内信:"+id);
        }
        notice.setReadIs(Boolean.TRUE);
        insideNoticeService.updateById(notice);

        Map<String, ApplyDto> clientMap = applyServiceApi.all().getData().stream().collect(Collectors.toMap(ApplyDto::getAppKey, Function.identity()));

        InsideNoticeDto insideNoticeDto = convert2Dto(notice);
        insideNoticeDto.setClientName(clientMap.get(insideNoticeDto.getClientCode()).getName());

        UserDto userDto = authUserServiceApi.getById(insideNoticeDto.getUserId()).getData();
        insideNoticeDto.setUserDto(userDto);
        return R.ok(insideNoticeDto);
    }

    @Override
    public R<Boolean> readIs(String id) {
        return R.ok(insideNoticeService.update(new LambdaUpdateWrapper<InsideNotice>().eq(InsideNotice::getId,id)
                .eq(InsideNotice::getUserId, UserCurrentUtils.getUserId())
                .eq(InsideNotice::getReadIs,Boolean.FALSE)
                .set(InsideNotice::getReadIs,Boolean.TRUE)));
    }

    @Override
    public R<Boolean> allReadIs() {
        return R.ok(insideNoticeService.update(new LambdaUpdateWrapper<InsideNotice>().eq(InsideNotice::getUserId,UserCurrentUtils.getUserId())
                .eq(InsideNotice::getReadIs,Boolean.FALSE)
                .set(InsideNotice::getReadIs,Boolean.TRUE)));
    }

    private LambdaQueryWrapper<InsideNotice> createQueryWrapper(InsideNoticeDto dto){
        return new LambdaQueryWrapper<InsideNotice>()
                .eq(InsideNotice::getUserId, UserCurrentUtils.getUserId())
                .like(StringUtil.isNotBlank(dto.getMsgContent()), InsideNotice::getMsgContent, dto.getMsgContent())
                .eq(StringUtil.isNotBlank(dto.getBatchNumber()), InsideNotice::getBatchNumber, dto.getBatchNumber())
                .eq(ObjectUtil.isNotNull(dto.getReadIs()), InsideNotice::getReadIs, dto.getReadIs())
                .eq(StringUtil.isNotBlank(dto.getClientCode()),InsideNotice::getClientCode,dto.getClientCode())
                .orderByDesc(InsideNotice::getCreateTime);
    }

    private InsideNoticeDto convert2Dto(InsideNotice entity){
        InsideNoticeDto dto = new InsideNoticeDto();
        BeanUtil.copyProperties(entity,dto);
        return dto;
    }

}
