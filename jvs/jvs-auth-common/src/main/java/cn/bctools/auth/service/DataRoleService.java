package cn.bctools.auth.service;

import cn.bctools.auth.entity.SysDataRole;
import cn.bctools.common.entity.dto.DataScopeDto;
import cn.bctools.common.entity.dto.UserDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author
 */
public interface DataRoleService extends IService<SysDataRole> {

    /**
     * 根据角色id集合获取数据权限信息
     *
     * @param user    用户信息
     * @param roleIds 角色id集合
     * @return 数据权限信息
     */
    List<DataScopeDto> queryUserPermission(UserDto user, List<String> roleIds);

}
