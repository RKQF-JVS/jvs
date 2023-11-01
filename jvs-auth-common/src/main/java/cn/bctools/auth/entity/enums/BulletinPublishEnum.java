package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ZhuXiaoKang
 * @Description: 公告发布状态
 */
@Getter
@AllArgsConstructor
public enum BulletinPublishEnum {

    /**
     * 公告发布状态
     */
    NO(0, "待发布"),
    YES(1, "发布"),
    ;

    @EnumValue
    @JsonValue
    public final Integer value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }
}
