package cn.bctools.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.bctools.log.po.LogPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 请求日志 Mapper 接口
 * </p>
 *
 * @author Administrator
 */
@Mapper
public interface SysLogDao extends BaseMapper<LogPo> {

}
