package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.SysTree;

/**
 * @author : GaoZeXi
 */
public interface SysTreeService extends IService<SysTree> {

    /**
     * 删除树形字典
     *
     * @param id 字典id
     */
    void deleteTree(String id);

    /**
     * 字典名称查重校验
     *
     * @param name     字典名称
     * @param parentId 上级节点id
     */
    void checkName(String name, String parentId);

}
