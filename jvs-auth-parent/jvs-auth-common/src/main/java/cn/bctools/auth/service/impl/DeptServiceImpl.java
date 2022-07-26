package cn.bctools.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.bctools.auth.entity.Dept;
import cn.bctools.auth.mapper.DeptMapper;
import cn.bctools.auth.service.DeptService;
import cn.bctools.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 部门服务
 *
 * @author
 */
@Slf4j
@Service
@AllArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public void clearUser(@NotNull String userId) {
        this.update(Wrappers.<Dept>lambdaUpdate().set(Dept::getLeaderId, null).eq(Dept::getLeaderId, userId));
    }

    @Override
    public Dept checkId(String deptId) {
        if (StringUtils.isBlank(deptId)) {
            throw new BusinessException("部门id为空, 操作失败");
        }
        Dept dept = this.getById(deptId);
        if (Objects.isNull(dept)) {
            log.error("该部门不存在, 部门id: {}", deptId);
            throw new BusinessException("该部门不存在");
        }
        return dept;
    }

}
