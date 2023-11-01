package cn.bctools.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.bctools.gateway.entity.TenantPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公司租户管理
 *
 * @author 
 */
@Mapper
public interface TenantMapper extends BaseMapper<TenantPo> {

}
