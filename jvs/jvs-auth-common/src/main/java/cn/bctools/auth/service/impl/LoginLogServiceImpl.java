package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.LoginLog;
import cn.bctools.auth.mapper.LoginLogMapper;
import cn.bctools.auth.service.LoginLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 
 */
@Slf4j
@Service
@AllArgsConstructor
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public LoginLog getLastLoginInfo(String userId) {
        return this.baseMapper.selectOne(Wrappers.<LoginLog>lambdaQuery()
                .eq(LoginLog::getUserId, userId)
                .orderByDesc(LoginLog::getOperateTime)
                .last("limit 1"));
    }

}
