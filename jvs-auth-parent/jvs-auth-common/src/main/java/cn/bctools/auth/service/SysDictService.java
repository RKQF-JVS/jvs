package cn.bctools.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.bctools.auth.entity.SysDict;
import cn.bctools.auth.entity.SysDictItem;

import java.util.List;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author guojing
 */
public interface SysDictService extends IService<SysDict> {


    /**
     * 根据ID 删除字典
     *
     * @param id
     * @return
     */
    void removeDict(String id);

    /**
     * 更新字典
     *
     * @param sysDict 字典
     * @return
     */
    void updateDict(SysDict sysDict);

    /**
     * 保存字典
     *
     * @param sysDict 字典
     * @return 字典id
     */
    String saveDict(SysDict sysDict);

    /**
     * 按类型获取字典
     * @param type 类型
     * @return 字典
     */
    List<SysDictItem> getDictByType(String type);
}
