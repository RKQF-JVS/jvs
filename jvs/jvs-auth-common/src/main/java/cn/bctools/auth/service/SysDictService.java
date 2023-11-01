package cn.bctools.auth.service;

import cn.bctools.auth.entity.SysDict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 保存字典
     *
     * @param sysDict 字典信息
     * @return 保存后的字典id
     */
    String saveDict(SysDict sysDict);

    /**
     * 根据id删除字典
     *
     * @param id 字典id
     */
    void removeDict(String id);

    /**
     * 更新字典
     *
     * @param sysDict 字典信息
     */
    void updateDict(SysDict sysDict);

}
