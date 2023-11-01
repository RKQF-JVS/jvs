package cn.bctools.auth.service;

import cn.bctools.auth.api.enums.SysDictEnum;
import cn.bctools.auth.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典项
 *
 * @author 
 */
public interface SysDictItemService extends IService<SysDictItem> {

    /**
     * 根据字典id查询字典项
     *
     * @param dictId 字典id
     * @return 字典项集合
     */
    List<SysDictItem> getByDictId(String dictId);

    /**
     * 根据字典id查询字典项
     *
     * @param type 字典类型
     * @return 字典项集合
     */
    List<SysDictItem> getByType(SysDictEnum type);

    /**
     * 根据字典id查询字典项
     *
     * @param type 字典类型
     * @return 字典项集合
     */
    List<SysDictItem> getByType(String type);

}
