package cn.bctools.auth.service;

import cn.bctools.gateway.entity.Apply;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author
 */
public interface ApplyService extends IService<Apply> {

    /**
     * 查询应用客户端是否存在
     *
     * @param clientId
     * @return
     */
    Apply loadClientByClientId(String clientId);

}
