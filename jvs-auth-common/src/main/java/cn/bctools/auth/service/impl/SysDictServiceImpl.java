package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.SysDict;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.entity.enums.DictTypeEnum;
import cn.bctools.auth.mapper.SysDictMapper;
import cn.bctools.auth.service.SysDictItemService;
import cn.bctools.auth.service.SysDictService;
import cn.bctools.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author guojing
 */
@Service
@AllArgsConstructor
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    SysDictItemService sysDictItemService;

    /**
     * 根据ID 删除字典
     *
     * @param id 字典ID
     */
    @Override
    @CacheEvict(value = "dict_details", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeDict(String id) {
        SysDict dict = this.getById(id);
        if (Objects.isNull(dict)) {
            return;
        }
        // 系统内置
        if (DictTypeEnum.SYSTEM.equals(dict.getSystem())) {
            throw new BusinessException("系统内置字典不能删除");
        }
        baseMapper.deleteById(id);
        sysDictItemService.remove(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
    }

    /**
     * 更新字典
     *
     * @param dict 字典
     */
    @Override
    public void updateDict(SysDict dict) {
        String newType = dict.getType();
        if (StringUtils.isBlank(newType)) {
            throw new BusinessException("类型不能为空");
        }
        SysDict sysDict = this.getById(dict.getId());
        if (Objects.isNull(sysDict)) {
            throw new BusinessException("该字典不存在");
        }
        String oldType = sysDict.getType();
        if (!newType.equals(oldType)) {
            Assert.isTrue(this.count(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getType, newType)) <= 0, String.format("该类型%s字典已经存在了", newType));
            // 同步字典项类型
            sysDictItemService.update(Wrappers.<SysDictItem>lambdaUpdate()
                    .set(SysDictItem::getType, newType)
                    .eq(SysDictItem::getType, oldType));
        }
        this.updateById(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveDict(SysDict sysDict) {
        String type = sysDict.getType();
        Assert.isTrue(this.count(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getType, type)) <= 0, String.format("该类型%s字典已经存在了", type));
        this.save(sysDict);
        return sysDict.getId();
    }

    @Override
    public List<SysDictItem> getDictByType(String type) {
        // 1. 查询当前租户
        List<SysDictItem> list = sysDictItemService.list(Wrappers.<SysDictItem>lambdaQuery()
                .eq(SysDictItem::getType, type)
                .orderByAsc(SysDictItem::getSort));
        if (!list.isEmpty()) {
            return list;
        }
        return new ArrayList<>();
    }

}
