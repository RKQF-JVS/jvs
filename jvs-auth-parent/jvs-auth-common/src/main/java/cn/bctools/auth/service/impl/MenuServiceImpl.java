package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.Menu;
import cn.bctools.auth.mapper.MenuMapper;
import cn.bctools.auth.service.MenuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author auto
 */
@Service
@AllArgsConstructor
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


}
