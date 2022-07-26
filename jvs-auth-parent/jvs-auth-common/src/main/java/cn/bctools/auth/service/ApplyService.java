package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.Apply;
import cn.bctools.auth.entity.Dept;

import java.util.List;

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
