package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.SysData;
import cn.bctools.auth.mapper.DataMapper;
import cn.bctools.auth.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author
 */
@Service
@AllArgsConstructor
public class DataServiceImpl extends ServiceImpl<DataMapper, SysData> implements DataService {

}
