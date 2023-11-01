package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.SysDictItem;
import cn.bctools.auth.mapper.SysDictItemMapper;
import cn.bctools.auth.service.SysDictItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 字典项
 *
 * @author guojing
 */
@Service
@Slf4j
@AllArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

}
