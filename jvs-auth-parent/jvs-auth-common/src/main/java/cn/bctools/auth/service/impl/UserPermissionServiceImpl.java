package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.bctools.auth.mapper.UserPermissionMapper;
import cn.bctools.auth.entity.UserPermission;
import cn.bctools.auth.service.UserPermissionService;
import org.springframework.stereotype.Service;

/**
 * 用户角色
 *
 * @author
 */
@Service
public class UserPermissionServiceImpl extends ServiceImpl<UserPermissionMapper, UserPermission> implements UserPermissionService {

}
