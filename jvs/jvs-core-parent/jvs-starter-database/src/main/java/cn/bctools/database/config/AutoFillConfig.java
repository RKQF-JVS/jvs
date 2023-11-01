package cn.bctools.database.config;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import cn.bctools.common.entity.dto.UserDto;
import cn.bctools.common.entity.dto.UserInfoDto;
import cn.bctools.common.utils.SystemThreadLocal;
import cn.bctools.common.utils.function.Get;
import cn.bctools.database.entity.po.BasePo;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.time.LocalDateTime;

/**
 * @Author: ZhuXiaoKang
 * @Description: 自动填充
*/
@ConditionalOnMissingBean(value = {MetaObjectHandler.class})
public class AutoFillConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, Get.name(BasePo::getCreateTime), () -> now, LocalDateTime.class);
        this.strictInsertFill(metaObject, Get.name(BasePo::getUpdateTime), () -> now, LocalDateTime.class);

        UserInfoDto<UserDto> user = SystemThreadLocal.get("user");
        if (ObjectUtil.isNotEmpty(user)) {
            UserDto userDto = user.getUserDto();
            this.strictInsertFill(metaObject, Get.name(BasePo::getDeptId), userDto::getDeptId, String.class);
            this.strictInsertFill(metaObject, Get.name(BasePo::getJobId), userDto::getJobId, String.class);
            this.strictInsertFill(metaObject, Get.name(BasePo::getCreateBy), userDto::getRealName, String.class);
            this.strictInsertFill(metaObject, Get.name(BasePo::getUpdateBy), userDto::getRealName, String.class);
            this.strictInsertFill(metaObject, Get.name(BasePo::getCreateById), userDto::getId, String.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 不使用严格模式填充策略, 直接覆盖updateTime字段
        metaObject.setValue(Get.name(BasePo::getUpdateTime), LocalDateTime.now());

        UserInfoDto<UserDto> user = SystemThreadLocal.get("user");
        if (ObjectUtil.isNotEmpty(user)) {
            UserDto userDto = user.getUserDto();
            this.strictUpdateFill(metaObject, Get.name(BasePo::getUpdateBy), userDto::getRealName, String.class);
        }
    }

}
