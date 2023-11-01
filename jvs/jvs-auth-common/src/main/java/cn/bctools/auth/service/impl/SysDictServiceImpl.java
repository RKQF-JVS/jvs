package cn.bctools.auth.service.impl;

import cn.bctools.auth.entity.SysDict;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.entity.enums.DictTypeEnum;
import cn.bctools.auth.mapper.SysDictMapper;
import cn.bctools.auth.service.SysDictItemService;
import cn.bctools.auth.service.SysDictService;
import cn.bctools.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 字典表 服务实现类
 *
 * @author 
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    SysDictItemService sysDictItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveDict(SysDict sysDict) {
        String type = sysDict.getType();
        Assert.isTrue(this.count(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getType, type)) <= 0, String.format("该类型%s字典已经存在了", type));
        this.save(sysDict);
        return sysDict.getId();
    }

    @Override
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
        this.removeById(id);
        sysDictItemService.remove(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
    }

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

}
