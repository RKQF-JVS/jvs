package cn.bctools.auth.service.impl;

import cn.bctools.auth.service.ApplyService;
import cn.bctools.gateway.entity.Apply;
import cn.bctools.gateway.mapper.ApplyMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author
 */
@Service
@AllArgsConstructor
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements ApplyService {
    @Override
    public Apply loadClientByClientId(String clientId) {
        return getOne(Wrappers.query(new Apply().setAppKey(clientId).setEnable(true)));
    }
}
