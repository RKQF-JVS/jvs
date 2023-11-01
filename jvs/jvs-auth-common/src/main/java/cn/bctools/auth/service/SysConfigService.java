package cn.bctools.auth.service;

import cn.bctools.auth.entity.SysConfig;
import cn.bctools.auth.entity.enums.SysConfigTypeEnum;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Author: ZhuXiaoKang
 * @Description:
 */
public interface SysConfigService extends IService<SysConfig> {
    /**
     * 保存配置
     *
     * @param tenantId 租户id
     * @param appId    应用id
     * @param type     配置类型
     * @param config   配置
     */
    void saveConfig(String tenantId, String appId, SysConfigTypeEnum type, Map<String, Object> config);

    /**
     * 获取租户所有应用配置
     *
     * @param tenantId 租户id
     * @return
     */
    Map<String, Map<String, Object>> getTenantApp(String tenantId);

    /**
     * 获取指定类型配置（包括保密字段）
     *
     * @param appId 应用id
     * @param type 配置类型
     * @return
     */
    Map<String, Object> getConfig(String appId, SysConfigTypeEnum type);

    /**
     * 获取指定类型配置
     *
     * @param appId   应用id
     * @param type    配置类型
     * @param secrecy TRUE-需要保密的字段（如密钥）不返回，FALSE-返回所有字段
     * @return
     */
    Map<String, Object> getConfig(String appId, SysConfigTypeEnum type, Boolean secrecy);

    /**
     * 获取应用所有开启扫码登录的配置类型
     *
     * @param appId 应用id
     * @return
     */
    List<SysConfigTypeEnum> getEnableScanConfigType(String appId);

    /**
     * 获取应用指定类型开启登录的配置类型
     *
     * @param appId
     * @param configTypes
     * @return
     */
    List<SysConfigTypeEnum> getEnableConfigType(String appId, List<SysConfigTypeEnum> configTypes);

    /**
     * 获取对象Key
     *
     * @param tenantId 当前租户ID
     * @param clientId
     * @param type     类型数据
     * @return
     */
    Map<String, Object> key(String tenantId, String clientId, SysConfigTypeEnum type);

}
