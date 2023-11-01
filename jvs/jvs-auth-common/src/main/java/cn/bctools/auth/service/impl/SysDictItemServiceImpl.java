package cn.bctools.auth.service.impl;

import cn.bctools.auth.api.enums.SysDictEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.mapper.SysDictItemMapper;
import cn.bctools.auth.service.SysDictItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 字典项
 *
 * @author 
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    @Override
    @CacheEvict(value = "dict_items_by_dictId", allEntries = true)
    public List<SysDictItem> getByDictId(String dictId) {
        return this.list(Wrappers.<SysDictItem>lambdaQuery()
                .eq(SysDictItem::getDictId, dictId)
                .orderByAsc(SysDictItem::getSort));
    }

    @Override
    public List<SysDictItem> getByType(SysDictEnum type) {
        if (Objects.isNull(type)) {
            return Collections.emptyList();
        }
        return this.getByType(type.name());
    }

    @Override
    @CacheEvict(value = "dict_items_by_type", allEntries = true)
    public List<SysDictItem> getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        return this.list(Wrappers.<SysDictItem>lambdaQuery()
                .eq(SysDictItem::getType, type)
                .orderByAsc(SysDictItem::getSort));
    }

}
