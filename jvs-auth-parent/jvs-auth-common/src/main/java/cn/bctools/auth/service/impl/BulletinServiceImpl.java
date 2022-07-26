package cn.bctools.auth.service.impl;

import cn.bctools.auth.entity.Bulletin;
import cn.bctools.auth.mapper.BulletinMapper;
import cn.bctools.auth.service.BulletinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @description 系统公告 服务实现类
 * @author zhuxiaokang
 * @since 2021-11-19
 */
@Service
public class BulletinServiceImpl extends ServiceImpl<BulletinMapper, Bulletin> implements BulletinService {

}
