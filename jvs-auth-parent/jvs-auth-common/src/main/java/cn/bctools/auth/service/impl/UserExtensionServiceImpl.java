package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.UserExtension;
import cn.bctools.auth.entity.UserTenant;
import cn.bctools.auth.mapper.UserExtensionMapper;
import cn.bctools.auth.mapper.UserTenantMapper;
import cn.bctools.auth.service.UserExtensionService;
import cn.bctools.auth.service.UserTenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class UserExtensionServiceImpl extends ServiceImpl<UserExtensionMapper, UserExtension> implements UserExtensionService {
}
