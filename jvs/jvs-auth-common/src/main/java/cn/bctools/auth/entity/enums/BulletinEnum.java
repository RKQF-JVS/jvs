package cn.bctools.auth.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 
 */

@Getter
@AllArgsConstructor
public enum BulletinEnum {

    /**
     * 公告发布状态
     */
    IMG("IMG", "图片"),
    TEXT("TEXT", "文本"),
    ;

    @EnumValue
    @JsonValue
    public final String value;
    public final String desc;

    @Override
    public String toString() {
        return desc;
    }
}
