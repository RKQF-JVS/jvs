package cn.bctools.demo.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import cn.bctools.database.interfaces.DataEnum;
import lombok.Getter;

/**
 * @author Administrator
 */
@Getter
public enum TypeEnum implements DataEnum {
    /**
     * 所有者、、
     */
    owner("owner", "所有者"),
    /**
     * 管理员
     **/
    admin("admin", "管理员"),
    /**
     * 成员
     **/
    member("member", "成员"),
    ;

    @EnumValue
    public final String value;
    public final String desc;

    TypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
