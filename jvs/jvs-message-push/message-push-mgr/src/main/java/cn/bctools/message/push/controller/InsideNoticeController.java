package cn.bctools.message.push.controller;

import cn.bctools.auth.api.api.ApplyServiceApi;
import cn.bctools.auth.api.api.AuthUserServiceApi;
import cn.bctools.auth.api.dto.ApplyDto;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.utils.R;
import cn.bctools.message.push.entity.InsideNotice;
import cn.bctools.message.push.service.InsideNoticeService;
import cn.bctools.oauth2.utils.AuthorityManagementUtils;
import cn.bctools.oauth2.utils.UserCurrentUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 站内消息通知 前端控制器
 * </p>
 *
 * @author wl
 * @since 2022-06-07
 */
@RestController
@RequestMapping("/station/inside/notice")
@Api(tags = "站内消息通知")
@AllArgsConstructor
public class InsideNoticeController {

    private final InsideNoticeService insideNoticeService;
    private final AuthUserServiceApi authUserServiceApi;
    private final ApplyServiceApi applyServiceApi;

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public R<Page<InsideNotice>> page(Page<InsideNotice> page, InsideNotice dto) {

        insideNoticeService.page(page, createQueryWrapper(dto));

        List<String> userIds = page.getRecords().stream().map(InsideNotice::getUserId).distinct().collect(Collectors.toList());
        //设置用户信息
        if (!userIds.isEmpty()) {
            Map<String, UserDto> userMap = AuthorityManagementUtils.getUsersByIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(UserDto::getId, Function.identity()));
            if (!userMap.isEmpty()) {
                page.getRecords().forEach(e -> e.setUserDto(userMap.get(e.getUserId())));
            }
        }
        this.setClientName(page.getRecords());
        return R.ok(page);
    }

    @GetMapping("/list")
    @ApiOperation("查询所有")
    public R<List<InsideNotice>> list(InsideNotice dto) {
        List<InsideNotice> insideNotices = insideNoticeService.list(createQueryWrapper(dto));
        List<String> userIds = insideNotices.stream().map(InsideNotice::getUserId).distinct().collect(Collectors.toList());
        //设置用户信息
        if (!userIds.isEmpty()) {
            Map<String, UserDto> userMap = AuthorityManagementUtils.getUsersByIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(UserDto::getId, Function.identity()));
            if (!userMap.isEmpty()) {
                insideNotices.forEach(e -> e.setUserDto(userMap.get(e.getUserId())));
            }
        }
        this.setClientName(insideNotices);
        return R.ok(insideNotices);
    }

    @GetMapping("/{id}/detail")
    @ApiOperation("查看详情")
    public R<InsideNotice> detail(@ApiParam("消息id") @PathVariable("id") String id) {
        InsideNotice insideNotice = insideNoticeService.getById(id);
        insideNotice.setReadIs(Boolean.TRUE);
        insideNoticeService.updateById(insideNotice);
        //查询用户信息
        UserDto data = authUserServiceApi.getById(insideNotice.getUserId()).getData();
        insideNotice.setUserDto(data);
        this.setClientName(insideNotice);
        return R.ok(insideNotice);
    }

    @GetMapping("/all/read")
    @ApiOperation("标记全部已读")
    public R<Boolean> allRead() {
        return R.ok(insideNoticeService.update(new LambdaUpdateWrapper<InsideNotice>()
                .eq(InsideNotice::getUserId, UserCurrentUtils.getUserId())
                .eq(InsideNotice::getReadIs, Boolean.FALSE)
                .set(InsideNotice::getReadIs, Boolean.TRUE)
        ));
    }

    @DeleteMapping("/del/{all}")
    @ApiOperation("删除消息")
    public R<Boolean> del(@ApiParam("all") @PathVariable("all") boolean all) {
        return R.ok(insideNoticeService.update(new LambdaUpdateWrapper<InsideNotice>()
                .set(InsideNotice::getDelFlag, Boolean.TRUE)
                .eq(InsideNotice::getUserId, UserCurrentUtils.getUserId())
                .eq(!all, InsideNotice::getReadIs, Boolean.TRUE)

        ));
    }


    @DeleteMapping("/del/single/{id}")
    @ApiOperation("删除单条消息")
    public R<Boolean> delSignle(@ApiParam("id") @PathVariable("id") String id) {
        //
        return R.ok(insideNoticeService.update(new LambdaUpdateWrapper<InsideNotice>()
                .set(InsideNotice::getDelFlag, Boolean.TRUE).eq(InsideNotice::getId, id)
        ));
    }

    private LambdaQueryWrapper<InsideNotice> createQueryWrapper(InsideNotice dto) {
        return new LambdaQueryWrapper<InsideNotice>()
                .eq(InsideNotice::getUserId, UserCurrentUtils.getUserId())
                .like(StrUtil.isNotBlank(dto.getMsgContent()), InsideNotice::getMsgContent, dto.getMsgContent())
                .eq(StrUtil.isNotBlank(dto.getBatchNumber()), InsideNotice::getBatchNumber, dto.getBatchNumber())
                .eq(ObjectUtil.isNotNull(dto.getReadIs()), InsideNotice::getReadIs, dto.getReadIs())
                .eq(StrUtil.isNotBlank(dto.getClientCode()), InsideNotice::getClientCode, dto.getClientCode())
                .eq(ObjectUtil.isNotNull(dto.getLargeCategories()),InsideNotice::getLargeCategories,dto.getLargeCategories())
                .eq(ObjectUtil.isNotNull(dto.getSubClass()),InsideNotice::getSubClass,dto.getSubClass())
                .orderByDesc(InsideNotice::getCreateTime);
    }

    /**
     * 设置客户端名称
     *
     * @param insideNotice 站内信消息
     */
    private void setClientName(InsideNotice insideNotice) {
        Map<String, String> applyMap = applyServiceApi.all().getData().stream().collect(Collectors.toMap(ApplyDto::getAppKey, ApplyDto::getName));
        insideNotice.setClientName(applyMap.getOrDefault(insideNotice.getClientCode(), "未知客户端"));
    }

    /**
     * 设置客户端名称
     *
     * @param insideNoticeList 站内信消息
     */
    private void setClientName(List<InsideNotice> insideNoticeList) {
        Map<String, String> applyMap = applyServiceApi.all().getData().stream().collect(Collectors.toMap(ApplyDto::getAppKey, ApplyDto::getName));
        insideNoticeList.forEach(e -> e.setClientName(applyMap.getOrDefault(e.getClientCode(), "未知客户端")));
    }
}
