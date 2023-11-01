package cn.bctools.oss.mapper;

import cn.bctools.oss.dto.BaseFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.bctools.oss.po.OssFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 
 * @describe
 */
@Mapper
public interface SysFileMapper extends BaseMapper<OssFile> {

    @Select("select bucket_name as bucketName,SUM(size) as size from sys_file where tenant_id = '${tenantId}' group by bucket_name ")
    List<BaseFile> selectSize(@Param("tenantId") String tenantId);
}

